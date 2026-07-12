package dev.byrt.survivalgames.item

import dev.byrt.survivalgames.map.MapDataPointType
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.Keys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object SGItem {
    fun getSupplyDropCompass(): ItemStack {
        val supplyDropCompass = ItemStack(Material.COMPASS, 1)
        val supplyDropCompassMeta = supplyDropCompass.itemMeta
        supplyDropCompassMeta.displayName(Formatting.allTags.deserialize("<!i><playercolour>${SG_FONT_TAG}Supply Drop Tracker</playercolour>"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}Tracks the latest supply drop."),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ff3333><unicodeprefix:warning> ${SG_FONT_TAG}Beware that it may already"),
            Formatting.allTags.deserialize("<!i><#ff3333>${SG_FONT_TAG}be looted or contested!"),
            Formatting.allTags.deserialize("<!i>")
        )
        supplyDropCompassMeta.lore(loreList)
        supplyDropCompassMeta.persistentDataContainer.set(Keys.SUPPLY_DROP_COMPASS, PersistentDataType.BOOLEAN, true)
        supplyDropCompass.itemMeta = supplyDropCompassMeta
        return supplyDropCompass
    }

    fun getSpectatorCompass(): ItemStack {
        val spectatorCompass = ItemStack(Material.RECOVERY_COMPASS, 1)
        val spectatorCompassMeta = spectatorCompass.itemMeta
        spectatorCompassMeta.displayName(Formatting.allTags.deserialize("<!i><playercolour>${SG_FONT_TAG}Spectator Compass</playercolour>"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}Press '<yellow><key:key.use></yellow>' to open"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}the spectator interface."),
            Formatting.allTags.deserialize("<!i>")
        )
        spectatorCompassMeta.lore(loreList)
        spectatorCompassMeta.persistentDataContainer.set(Keys.SPECTATOR_COMPASS, PersistentDataType.BOOLEAN, true)
        spectatorCompass.itemMeta = spectatorCompassMeta
        return spectatorCompass
    }


    fun getLantern(): ItemStack {
        val lantern = ItemStack(Material.LANTERN, 1)
        val lanternMeta = lantern.itemMeta
        lanternMeta.displayName(Formatting.allTags.deserialize("<!i><playercolour>${SG_FONT_TAG}Mistwoods Lantern</playercolour>"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}While holding this lantern,"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}the bearer is able to see"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}their nearby surroundings."),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ff3333>${SG_FONT_TAG}Beware of opponents lurking"),
            Formatting.allTags.deserialize("<!i><#ff3333>${SG_FONT_TAG}in the darkness..."),
        )
        lanternMeta.lore(loreList)
        lanternMeta.persistentDataContainer.set(Keys.LANTERN, PersistentDataType.BOOLEAN, true)
        lantern.itemMeta = lanternMeta
        return lantern
    }

    fun getDataPointItem(dataPointType: MapDataPointType): ItemStack {
        val dataPointItem = ItemStack(Material.STICK, 1)
        val dataPointItemMeta = dataPointItem.itemMeta
        dataPointItemMeta.displayName(Formatting.allTags.deserialize("<!i><playercolour>${SG_FONT_TAG}Data Point Tool</playercolour>"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}Create data points when in an"),
            Formatting.allTags.deserialize("<!i><#ffff00>${SG_FONT_TAG}edit mode enabled container."),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><red>${SG_FONT_TAG}Active data point type: '${dataPointType.typeName}'."),
            Formatting.allTags.deserialize("<!i>")
        )
        dataPointItemMeta.lore(loreList)
        dataPointItemMeta.persistentDataContainer.set(Keys.DATA_POINT_EDIT, PersistentDataType.STRING, dataPointType.name)
        dataPointItemMeta.tooltipStyle = Keys.ADMIN_TOOLTIP
        dataPointItem.itemMeta = dataPointItemMeta
        return dataPointItem
    }
}