package dev.byrt.survivalgames.util

import dev.byrt.survivalgames.plugin
import org.bukkit.NamespacedKey

object Keys {
    /**
     * Data Point related
     */
    val DATA_POINT_EDIT = NamespacedKey(plugin, "data_point.edit")
    /**
     * Gameplay related
     */
    val SUPPLY_DROP_COMPASS = NamespacedKey(plugin, "supply_drop_compass")
    val SPECTATOR_COMPASS = NamespacedKey(plugin, "spectator_compass")
    val PARTICIPANT_UUID = NamespacedKey(plugin, "participant_uuid")
    val LANTERN = NamespacedKey(plugin, "lantern")
    val LOOT_ITEM = NamespacedKey(plugin, "loot")
    /**
     * Tooltip related, not using plugin namespace as they must match the resource pack namespace.
     */
    val COMMON_TOOLTIP = NamespacedKey("sg", "common")
    val UNCOMMON_TOOLTIP = NamespacedKey("sg", "uncommon")
    val RARE_TOOLTIP = NamespacedKey("sg", "rare")
    val EPIC_TOOLTIP = NamespacedKey("sg", "epic")
    val LEGENDARY_TOOLTIP = NamespacedKey("sg", "legendary")
    val MYTHIC_TOOLTIP = NamespacedKey("sg", "mythic")
    val ADMIN_TOOLTIP = NamespacedKey("sg", "admin")
    /**
     * Recipe related
     */
    val IRON_SWORD_RECIPE = NamespacedKey(plugin, "recipe.iron_sword")
    val IRON_HELMET_RECIPE = NamespacedKey(plugin, "recipe.iron_helmet")
    val IRON_CHESTPLATE_RECIPE = NamespacedKey(plugin, "recipe.iron_chestplate")
    val IRON_LEGGINGS_RECIPE = NamespacedKey(plugin, "recipe.iron_leggings")
    val IRON_BOOTS_RECIPE = NamespacedKey(plugin, "recipe.iron_boots")
    val DIAMOND_SWORD_RECIPE = NamespacedKey(plugin, "recipe.diamond_sword")
    /**
     * Lobby related
     */
    val CONTAINER_ID = NamespacedKey(plugin, "container_id")
    val LOBBY_NPC = NamespacedKey(plugin, "lobby_npc")
}