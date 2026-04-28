package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.text.Formatting
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
/** Dummy command **/
class Echo {
    @Command("echo <text>")
    @Permission("sg.cmd.echo")
    fun echo(sender: CommandSender, text: Array<String>) {
        sender.sendMessage(Formatting.allTags.deserialize(text.joinToString(" ")))
    }
}