package dev.byrt.survivalgames.team

import dev.byrt.survivalgames.game.GameContainer
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
    override var container: GameContainer?
) : GameTeam, ComponentLike {
    PLAYER(
        teamDisplayName = "Player",
        playerNamePrefix = Component.empty(),
        teamColour = Color.YELLOW,
        textColour = NamedTextColor.YELLOW,
        container = null
    ), ;

    override fun asComponent() = Component.translatable("sg.team.normal.${name.lowercase()}")

    fun uppercaseName() = Component.translatable("sg.team.uppercase.${name.lowercase()}")
}