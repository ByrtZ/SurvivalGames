package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.Formatting.SG_FONT
import dev.byrt.survivalgames.text.TextAlignment
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.Style
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class AdminCommands {
    @Command("ac <text>")
    @CommandDescription("Changes the value of the specified setting.")
    @Permission("sg.cmd.admin_chat")
    fun adminChat(sender: Player, @Argument("text") text: Array<String>) {
        ChatUtility.broadcastAdmin(
            "<skull:${sender.name}><dark_red>${sender.name}<white>: ${text.joinToString(" ")}",
            false
        )
    }

    @Command("dc <text>")
    @CommandDescription("Changes the value of the specified setting.")
    @Permission("sg.cmd.dev_chat")
    fun devChat(sender: Player, @Argument("text") text: Array<String>) {
        ChatUtility.broadcastDev(
            "<skull:${sender.name}><gold>${sender.name}<white>: ${text.joinToString(" ")}",
            false
        )
    }

    @Command("debug tasks")
    @CommandDescription("Lists all running tasks")
    @Permission("sg.cmd.debug")
    fun debugTasks(sender: CommandSender) {
        ChatUtility.broadcastDev(
            "<dark_gray>Tasks printed to console, <click:open_url:'https://panel.pebblehost.com'>[Click here]</click> to view.",
            true
        )
        logger.warning("Active Workers:")
        Bukkit.getScheduler().activeWorkers.forEach { w -> logger.info("ID: ${w.taskId} ||||| Thread: ${w.thread} ||||| Thread name: ${w.thread.name} ||||| Owner: ${w.owner.name}") }
        logger.warning("Pending Tasks:")
        Bukkit.getScheduler().pendingTasks.forEach { t -> logger.info("ID: ${t.taskId} ||||| Sync: ${t.isSync} ||||| Owner: ${t.owner.name} ||||| Cancelled: ${t.isCancelled}") }
    }

    @Command("debug bossbar <text>")
    @CommandDescription("Shows temporary test bossbar")
    @Permission("sg.cmd.debug")
    fun debugBossbar(sender: Player, @Argument("text") @Greedy text: String) {
        val text = TextAlignment.tinsel.draw(100, Style.empty()) {
            it.drawAligned(Formatting.glyph("\uD011").shadowColor(ShadowColor.none()), 0.5f)
            it.drawAligned(
                Component.text(text).font(SG_FONT),
                0.5f,
            )
        }

        val tempBossBar = BossBar.bossBar(
            text,
            0f,
            BossBar.Color.WHITE,
            BossBar.Overlay.PROGRESS
        ).apply {
            addViewer(Audience.audience(sender))
        }
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    tempBossBar.removeViewer(player)
                }
            }
        }.runTaskLater(plugin, 200L)
    }

    @Command("debug translation <translation>")
    @Permission("sg.cmd.debug")
    fun sendTranslation(player: Player, @Argument("translation") translation: String) {
        player.sendMessage(Component.translatable(translation))
    }
}
