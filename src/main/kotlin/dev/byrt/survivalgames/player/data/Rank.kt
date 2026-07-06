package dev.byrt.survivalgames.player.data

import dev.byrt.survivalgames.item.rarity.ItemRarity
import dev.byrt.survivalgames.text.Formatting.GLYPH_FONT

enum class Rank(val rankName: String, val rankPlate: String, val rankBadge: String, val rankHexColour: String, val rankHexTag: String) {
    RECRUIT("Recruit", "RECRUIT", "R", ItemRarity.COMMON.rarityColour, "<${ItemRarity.COMMON.rarityColour}>"),
    SOLDIER("Soldier", "SOLDIER", "S", "#5dd465", "<#5dd465>"),
    CONSTABLE("Constable", "CONSTABLE", "C", "#639cf2", "<#639cf2>"),
    WINGS("Wings", "WINGS", "W", "#ed8218", "<#ed8218>"),
    GENDARME("Gendarme", "GENDARME", "G", "#974dff", "<#974dff>"),
    ROYAL("Royal", "ROYAL", "★", "#ff0044", "<#ff0044>");

    fun Rank.plateGlyph(): String = "<!shadow><font:$GLYPH_FONT>${this.rankPlate}</font></!shadow>"
    fun Rank.badgeGlyph(): String = "<!shadow><font:$GLYPH_FONT>${this.rankBadge}</font></!shadow>"
}