package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.team.Team
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class TeamsCommands {
    @Command("teams set <player> <team>")
    @CommandDescription("Puts the specified player on the specified team.")
    @Permission("sg.cmd.teams")
    fun setTeam(sender: CommandSender, @Argument("player") player: Player, @Argument("team") team: Team) {
        sender.sendMessage(Formatting.allTags.deserialize("<red>Teams unavailable to be modified manually."))
    }

    @Command("teams remove <player>")
    @CommandDescription("Removes a player from their team.")
    @Permission("sg.cmd.teams")
    fun removeFromTeam(sender: CommandSender, @Argument("player") player: Player) {
        sender.sendMessage(Formatting.allTags.deserialize("<red>Teams unavailable to be modified manually."))
    }
}