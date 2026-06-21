package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.plugin
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent

@Suppress("unused")
class CraftEvent: Listener {
    @EventHandler
    private fun onPrepareCraft(e: PrepareItemCraftEvent) {
        val recipe = e.recipe ?: return
        if(recipe !is Keyed || recipe.key.namespace != plugin.name.lowercase())  {
            e.inventory.result = null
        }
    }
}