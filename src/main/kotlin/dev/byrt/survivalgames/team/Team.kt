package dev.byrt.survivalgames.team

import dev.byrt.survivalgames.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color

enum class Team (
    override val teamDisplayName: String,
    val playerNamePrefix: Component,
    val teamColour: Color,
    override val textColour: TextColor,
) : GameTeam, ComponentLike {
    PLAYER(
        teamDisplayName = "Player",
        playerNamePrefix = Formatting.glyph("\uD012"),
        teamColour = Color.YELLOW,
        textColour = NamedTextColor.YELLOW
    ), ;

    override fun asComponent() = Component.translatable("sg.team.normal.${name.lowercase()}")

    fun uppercaseName() = Component.translatable("sg.team.uppercase.${name.lowercase()}")
}