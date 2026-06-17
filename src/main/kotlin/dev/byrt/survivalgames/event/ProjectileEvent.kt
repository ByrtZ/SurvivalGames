package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import org.bukkit.Particle
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class ProjectileEvent: Listener {
    @EventHandler
    private fun onPearlThrow(e: ProjectileLaunchEvent) {
        if(e.entity is EnderPearl && e.entity.shooter is Player) {
            val pearl = e.entity as EnderPearl
            val shooter = e.entity.shooter as Player
            if(shooter.sgPlayer().currentContainer != null) {
                object : BukkitRunnable() {
                    override fun run() {
                        if(pearl.isDead || pearl.isOnGround || pearl.world != shooter.world) {
                            cancel()
                        }
                        pearl.location.world.spawnParticle(
                            Particle.WITCH,
                            pearl.location,
                            2,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            null,
                            true
                        )
                    }
                }.runTaskTimer(plugin, 0L, 1L)
            }
        }
    }
}