package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import java.time.Duration

@CommandContainer
@Suppress("unused", "unstableApiUsage")
class PlayerCommands {
    @Command("player <player>")
    @CommandDescription("Gets all information about a player that is stored.")
    @Permission("sg.cmd.player")
    fun getPlayer(sender: Player, @Argument("player") player: Player) {
        sender.sendMessage(Formatting.allTags.deserialize("<sgcolour><bold>Player ${player.name}'s Info:<reset>\n<speccolour>Name: <yellow>${player.sgPlayer().playerName}<speccolour>\nUUID: <yellow>${player.sgPlayer().uuid}<speccolour>\nClient: <yellow>${player.clientBrandName}<speccolour>\nType: <yellow>${player.sgPlayer().playerType}<speccolour>\nDead: <yellow>${player.sgPlayer().isDead}<speccolour>\nContainer: <yellow>${player.sgPlayer().currentContainer?.containerId}<speccolour>\n"))
    }

    @Command("hub")
    @CommandDescription("Return to the hub.")
    fun hub(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            sender.showTitle(Title.title(
                Formatting.glyph("\uD001"),
                Component.empty(), Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250))
            ))
            object : BukkitRunnable() {
                override fun run() {
                    GameManager.removePlayerFromContainer(sender)
                }
            }.runTaskLater(plugin, 10L)
        }
    }
}