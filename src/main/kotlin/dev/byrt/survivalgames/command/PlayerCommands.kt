package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.interfaces.SGMatchmakingInterface
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import kotlinx.coroutines.launch
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
    @Command("info <player>")
    @CommandDescription("Gets all information about a player that is stored.")
    @Permission("sg.cmd.player")
    fun getPlayer(sender: Player, @Argument("player") player: Player) {
        sender.sendMessage(Formatting.allTags.deserialize("<newline><playercolour><bold>Player ${player.name}'s Info:<reset><newline><speccolour>Name: <yellow>${player.sgPlayer().playerName}<speccolour><newline>UUID: <yellow>${player.sgPlayer().uuid}<speccolour><newline>Client: <yellow>${player.clientBrandName}<speccolour><newline>Type: <yellow>${player.sgPlayer().playerType}<speccolour><newline>Dead: <yellow>${player.sgPlayer().isDead}<speccolour><newline>Container: <yellow>${player.sgPlayer().currentContainer?.containerId}<speccolour><newline>Rank: ${player.sgPlayer().rank.rankHexTag}${player.sgPlayer().rank.rankName}<speccolour><newline>Level: <yellow>${player.sgPlayer().level.levelName}<speccolour><newline>XP: <yellow>${player.sgPlayer().exp}<speccolour><newline>Matches Played: <yellow>${player.sgPlayer().matchesPlayed}<speccolour><newline>Wins: <yellow>${player.sgPlayer().wins}<speccolour><newline>Eliminations: <yellow>${player.sgPlayer().eliminations}<speccolour><newline>"))
    }

    @Command("hub")
    @CommandDescription("Return to the hub.")
    fun hub(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            sender.showTitle(Title.title(
                Formatting.glyph("\uD000"),
                Component.empty(), Title.Times.times(Duration.ofMillis(50), Duration.ofSeconds(1), Duration.ofMillis(250))
            ))
            object : BukkitRunnable() {
                override fun run() {
                    GameManager.removePlayerFromContainer(sender)
                }
            }.runTaskLater(plugin, 10L)
        }
    }

    @Command("matchmaking")
    @CommandDescription("Opens the matchmaker.")
    fun matchmaking(sender: Player) {
        if(sender.sgPlayer().currentContainer == null) {
            defaultCoroutineScope.launch {
                SGMatchmakingInterface.create(sender)
            }
        }
    }
}