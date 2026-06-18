package dev.byrt.survivalgames.event

import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDismountEvent

@Suppress("unused")
class DismountEvent: Listener {
    @EventHandler
    private fun onDismount(e: EntityDismountEvent) {
        if(e.entity is Player && e.dismounted is ItemDisplay) {
            e.isCancelled = true
        }
    }
}