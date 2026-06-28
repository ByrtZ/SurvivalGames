package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.paper.util.sender.PlayerSource
import org.incendo.cloud.processors.confirmation.annotation.Confirmation
import org.incendo.cloud.suggestion.Suggestion

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class ContainerCommands {
    @Command("container create")
    @CommandDescription("Creates a new game container.")
    @Permission("burb.cmd.container")
    @Confirmation
    suspend fun createContainer(sender: Player, @Flag("map") forcedMap: SGMap? = null, @Flag("editMode") isEditMode: Boolean = false, @Flag("disableGameEndCheck") disableGameEndCheck: Boolean = false) {
        if(sender.sgPlayer().currentContainer == null) {
            val container = GameManager.createContainer(isEditMode, forcedMap, disableGameEndCheck)
            ChatUtility.broadcastDev("<dark_gray>${sender.name} created a container (${container.containerId})${if(isEditMode) " (Edit Mode)" else ""}${if (forcedMap != null) " (${forcedMap.mapName})" else ""}${if (disableGameEndCheck) " (Game End check disabled)" else ""}", false)
        }
    }

    @Command("container destroy <container>")
    @CommandDescription("Destroys the specified game container.")
    @Permission("burb.cmd.container")
    @Confirmation
    fun destroyContainer(sender: Player, @Argument(value = "container",  suggestions = "containers") container: String) {
        ChatUtility.broadcastDev("<dark_gray>${sender.name} destroyed container (${container})", false)
        GameManager.getContainerById(container)?.let { GameManager.destroyContainer(it) }
    }

    @Command("container destroy all")
    @CommandDescription("Destroys all game containers.")
    @Permission("burb.cmd.container")
    @Confirmation
    fun destroyAllContainers(sender: Player) {
        ChatUtility.broadcastDev("<dark_gray>${sender.name} destroyed all containers", false)
        GameManager.gameContainers.forEach{ container -> GameManager.destroyContainer(container) }
    }

    @Command("container join <container>")
    @CommandDescription("Allows executing player to join specified container.")
    @Permission("burb.cmd.container")
    fun joinContainer(sender: Player, @Argument(value = "container",  suggestions = "containers") container: String) {
        if(sender.sgPlayer().currentContainer?.containerId?.toString() != container) {
            GameManager.getContainerById(container)?.let { GameManager.addPlayerToContainer(sender, it) }
        }
    }

    @Command("container leave")
    @CommandDescription("Allows executing player to leave their current container.")
    @Permission("burb.cmd.container")
    fun leaveContainer(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            GameManager.removePlayerFromContainer(sender)
        }
    }

    @Command("move <container>")
    @CommandDescription("Moves all players not in a container to the specified container.")
    @Permission("burb.cmd.container")
    fun moveToContainer(sender: Player, @Argument(value = "container",  suggestions = "containers") container: String) {
        if(sender.sgPlayer().currentContainer?.containerId?.toString() != container) {
            if(GameManager.getContainerById(container) != null) {
                for(player in Bukkit.getOnlinePlayers()) {
                    if(player.sgPlayer().currentContainer == null) {
                        GameManager.addPlayerToContainer(player, GameManager.getContainerById(container)!!)
                    }
                }
                ChatUtility.broadcastDev("<dark_gray>${sender.name} moved all applicable to container (${container})", false)
            }
        }
    }

    @Suggestions("containers")
    fun activeContainers(commandContext: CommandContext<PlayerSource>, input: String): List<Suggestion> {
        return GameManager.gameContainers.map { Suggestion.suggestion(it.containerId.toString()) }
    }
}