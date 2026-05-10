package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.processors.confirmation.annotation.Confirmation

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class ContainerCommands {
    @Command("container create")
    @CommandDescription("Creates a new game container.")
    @Permission("burb.cmd.container")
    @Confirmation
    fun createContainer(sender: Player) {
        if(sender.sgPlayer().currentContainer == null) {
            val container = GameManager.createContainer()
            ChatUtility.broadcastDev("<dark_gray>${sender.name} created a container (${container.containerId})", false)
        }
    }

    @Command("container destroy <container>")
    @CommandDescription("Destroys the specified game container.")
    @Permission("burb.cmd.container")
    @Confirmation
    fun destroyContainer(sender: Player, @Argument(value = "container") container: GameContainer) {
        ChatUtility.broadcastDev("<dark_gray>${sender.name} destroyed container (${container.containerId})", false)
        GameManager.destroyContainer(container)
    }

    @Command("container join <container>")
    @CommandDescription("Allows executing player to join specified container.")
    @Permission("burb.cmd.container")
    @Confirmation
    fun joinContainer(sender: Player, @Argument(value = "container") container: GameContainer) {
        if(sender.sgPlayer().currentContainer != container) {
            GameManager.addPlayerToContainer(sender, container)
            ChatUtility.broadcastDev("<dark_gray>${sender.name} joined container (${container.containerId})", false)
        }
    }

    @Command("container leave")
    @CommandDescription("Allows executing player to leave their current container.")
    @Permission("burb.cmd.container")
    @Confirmation
    fun leaveContainer(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            ChatUtility.broadcastDev("<dark_gray>${sender.name} left container (${sender.sgPlayer().currentContainer?.containerId})", false)
            GameManager.removePlayerFromContainer(sender)
        }
    }
}