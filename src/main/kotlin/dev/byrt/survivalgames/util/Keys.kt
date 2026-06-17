package dev.byrt.survivalgames.util

import dev.byrt.survivalgames.plugin
import org.bukkit.NamespacedKey

object Keys {
    /**
     * Data Point related
     */
    val DATA_POINT_EDIT = NamespacedKey(plugin, "sg.data_point.edit")
    /**
     * Gameplay related
     */
    val SUPPLY_DROP_COMPASS = NamespacedKey(plugin, "sg.supply_drop_compass")
    /**
     * Lobby related
     * */
    val CONTAINER_ID = NamespacedKey(plugin, "sg.container_id")
    val LOBBY_NPC = NamespacedKey(plugin, "sg.lobby_npc")
}