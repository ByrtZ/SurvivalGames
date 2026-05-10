package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.world.SGWorld
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.paper.util.sender.PlayerSource
import org.incendo.cloud.processors.confirmation.annotation.Confirmation
import org.incendo.cloud.suggestion.Suggestion
import java.util.*

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class ContainerCommands {
    @Command("container create")
    @CommandDescription("Creates a new game container.")
    @Permission("burb.cmd.container")
    @Confirmation
    suspend fun createContainer(sender: Player) {
        if(sender.sgPlayer().currentContainer == null) {
            val container = GameManager.createContainer()
            ChatUtility.broadcastDev("<dark_gray>${sender.name} created a container (${container.containerId})", false)
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

    @Command("container join <container>")
    @CommandDescription("Allows executing player to join specified container.")
    @Permission("burb.cmd.container")
    fun joinContainer(sender: Player, @Argument(value = "container",  suggestions = "containers") container: String) {
        if(sender.sgPlayer().currentContainer?.containerId?.toString() != container) {
            GameManager.getContainerById(container)?.let { GameManager.addPlayerToContainer(sender, it) }
            ChatUtility.broadcastDev("<dark_gray>${sender.name} joined container (${container})", false)
        }
    }

    @Command("container leave")
    @CommandDescription("Allows executing player to leave their current container.")
    @Permission("burb.cmd.container")
    fun leaveContainer(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            ChatUtility.broadcastDev("<dark_gray>${sender.name} left container (${sender.sgPlayer().currentContainer?.containerId})", false)
            GameManager.removePlayerFromContainer(sender)
        }
    }

    @Command("debug generate_world")
    @Permission("sg.cmd.debug")
    suspend fun debugGenerateWorld(sender: Player) {
        val worldID = UUID.randomUUID()
        SGWorld.createNewGameWorld(worldID)
    }

    @Command("debug join_world <id>")
    @Permission("sg.cmd.debug")
    fun debugJoinWorld(sender: Player, @Argument("id", suggestions = "containers") id: String) {
        val world = Bukkit.getWorld("sg-game-$id")
        if(world != null) {
            sender.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
        }
    }

    @Suggestions("containers")
    fun activeContainers(commandContext: CommandContext<PlayerSource>, input: String): List<Suggestion> {
        return GameManager.gameContainers.map { Suggestion.suggestion(it.containerId.toString()) }
    }
}