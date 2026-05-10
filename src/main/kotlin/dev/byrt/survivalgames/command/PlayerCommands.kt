package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@CommandContainer
@Suppress("unused", "unstableApiUsage")
class PlayerCommands {
    @Command("player <player>")
    @CommandDescription("Gets all information about a player that is stored.")
    @Permission("sg.cmd.player")
    fun getPlayer(sender: CommandSender, @Argument("player") player: Player) {
        sender.sendMessage(Formatting.allTags.deserialize("<sgcolour><bold>Player ${player.name}'s Info:<reset>\n<speccolour>Name: <yellow>${player.sgPlayer().playerName}<speccolour>\nUUID: <yellow>${player.sgPlayer().uuid}<speccolour>\nClient: <yellow>${player.clientBrandName}<speccolour>\nType: <yellow>${player.sgPlayer().playerType}<speccolour>\nDead: <yellow>${player.sgPlayer().isDead}<speccolour>\nContainer: <yellow>${player.sgPlayer().currentContainer?.containerId}<speccolour>\n"))
    }
}