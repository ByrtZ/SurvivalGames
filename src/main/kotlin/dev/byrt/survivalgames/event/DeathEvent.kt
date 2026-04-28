package dev.byrt.survivalgames.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

@Suppress("unused")
class DeathEvent : Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        //TODO Custom deaths
        /*e.player.killer?.let {
            it.playSound(Sounds.Score.ELIMINATION)
            Scores.addScore(GameManager.teams.getTeam(it.uniqueId) ?: return, 50)
            BurbExperienceLevels.appendExperience(it, 50)
        }

        PlayerVisuals.death(e.player, e.player.killer, true)
        e.isCancelled = true*/
    }
}