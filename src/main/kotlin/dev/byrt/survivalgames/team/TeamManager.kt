package dev.byrt.survivalgames.team

import dev.byrt.survivalgames.game.instance.GameInstanceInfo
import dev.byrt.survivalgames.logger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.Team
import java.time.Duration
import java.util.UUID
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries
import kotlin.reflect.KClass

/**
 * Manages the team a player is assigned to.
 */
class TeamManager<T> @PublishedApi internal constructor(
    private val teamClazz: KClass<T>,
    private val allTeams: EnumEntries<T>
) : Listener where T : GameTeam, T : Enum<T> {

    companion object {
        inline operator fun <reified T> invoke() where T : GameTeam, T : Enum<T> =
            TeamManager(T::class, enumEntries<T>())
    }

    private val playerTeams = mutableMapOf<UUID, T>()

    private val scoreboardTeams = allTeams.associateWith {
        GameInstanceInfo.scoreboard.registerNewTeam(it.name).apply {
            displayName(Component.text(it.name))
            setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
            color(NamedTextColor.nearestTo(it.textColour))
        }
    }

    /**
     * Whether a player is participating in the game, i.e. they are on a team.
     */
    fun isParticipating(player: UUID): Boolean = player in playerTeams

    /**
     * A set of all participating players.
     */
    //fun allParticipants(): Set<BurbPlayer> = playerTeams.keys.mapTo(mutableSetOf()) { it.burbPlayer() }

    /**
     * Gets all the players on a team.
     */
    //fun teamMembers(team: T) : Set<BurbPlayer> = playerTeams.filterValues { it == team }.keys.mapTo(mutableSetOf()) { it.burbPlayer() }

    /**
     * Gets the team the player is on.
     */
    fun getTeam(player: UUID): T? = playerTeams[player]

    /**
     * Sets the team the player is on.
     */
    fun setTeam(player: Player, team: T?) {
        val previousTeam = if (team == null) {
            playerTeams.remove(player.uniqueId)
        } else {
            playerTeams.put(player.uniqueId, team)
        }

        if (previousTeam != null) {
            scoreboardTeams.getValue(previousTeam).removePlayer(player)
        }

        Bukkit.getPluginManager().callEvent(PlayerTeamChangedEvent(player, team))
        val teamChangeComponent = teamChangeComponent(team)
        player.sendMessage(teamChangeComponent)
        player.showTitle(Title.title(
            Component.empty(),
            teamChangeComponent,
            Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250)),
        ))
        if (team != null) {
            scoreboardTeams.getValue(team).addPlayer(player)
        }
        logger.info("Teams: ${player.name} now has value ${team?.name}.")
    }

    /** Return component depending on what team is set **/
    fun teamChangeComponent(team: T?): Component = team?.let { Component.translatable("sg.team.join", it) } ?: Component.translatable("sg.team.leave")

    /**
     * Remove players from their team on quit.
     */
    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        setTeam(event.player, null)
    }
}