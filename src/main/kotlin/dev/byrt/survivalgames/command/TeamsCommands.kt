package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.game.GameState
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
        if (GameManager.getGameState() == GameState.IDLE) {
            GameManager.teams.setTeam(player, team)
        } else {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams remove <player>")
    @CommandDescription("Removes a player from their team.")
    @Permission("sg.cmd.teams")
    fun removeFromTeam(sender: CommandSender, @Argument("player") player: Player) {
        if (GameManager.getGameState() == GameState.IDLE) {
            GameManager.teams.setTeam(player, null)
        } else {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }
}