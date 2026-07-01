package dev.byrt.survivalgames.nametag.provider

import dev.byrt.survivalgames.nametag.NameTagProvider
import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.TextAlignment
import me.lucyydotp.tinsel.font.Spacing
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.attribute.Attribute
import kotlin.math.floor
import kotlin.math.roundToInt

class GameNameTagProvider : NameTagProvider() {
    private companion object {
        private const val BAR_WIDTH = 60
        private const val FILL_END_CHAR = '\uE001'
        private const val FILL_CHAR = '\uE002'
        private const val FILL_TICK_CENTER_CHAR = '\uE003'
        private const val FILL_TICK_LARGE_CHAR = '\uE004'
        private const val FILL_TICK_SMALL_CHAR = '\uE005'
        private const val SPACE_CHAR = '\uF8FF'
    }
    override val lines = 2

    override fun update(player: SGPlayer) {
        val tag = player.nameTag ?: return
        tag[0] = drawHealthbar(player)
        tag[0].backgroundColor = Color.fromARGB(0)
        tag[0].isShadowed = false
        tag[1] = Formatting.allTags.deserialize("${player.rank.rankHexTag}<b>${player.rank.rankBadge}</b> <playercolour>${player.playerName}")
    }

    private fun buildString(startingIndex: Int, size: Int) = buildString {
        (startingIndex..<startingIndex + size).forEach { i ->
            append(
                when {
                    i == 0 || i == BAR_WIDTH - 1 -> FILL_END_CHAR
                    i == BAR_WIDTH / 2 -> FILL_TICK_CENTER_CHAR
                    i % (BAR_WIDTH / 5) == 0 -> FILL_TICK_LARGE_CHAR
                    i % (BAR_WIDTH / 10) == 0 -> FILL_TICK_SMALL_CHAR
                    else -> FILL_CHAR
                }
            )
            append(SPACE_CHAR)
        }
    }

    private fun drawHealthbar(sgPlayer: SGPlayer, health: Double = sgPlayer.bukkitPlayer().health): Component {
        val player = sgPlayer.bukkitPlayer()
        val out = Component.text().font(Key.key("sg", "healthbar"))
        val healthPct = (health / player.getAttribute(Attribute.MAX_HEALTH)!!.value).coerceIn(0.0, 1.0)
        val fillAmount = ((BAR_WIDTH * healthPct).roundToInt() + 1).coerceAtMost(BAR_WIDTH)
        out.append(
            Formatting.allTags.deserialize("<playercolour>${buildString(0, fillAmount)}")
        )
        out.append(
            Formatting.allTags.deserialize("<dark_gray>${buildString(fillAmount, BAR_WIDTH - fillAmount)}")
        )

        out.append(Spacing.spacing(-BAR_WIDTH - 1))
        out.append(Component.text("\uE000"))

        val healthText = Component.text(floor(health).toInt().toString()).font(Formatting.SG_FONT)
        val healthOffset = (TextAlignment.tinsel.textWidthMeasurer().measure(healthText) / 2f).roundToInt()

        out.append(Spacing.spacing(-(healthOffset + 7)))
        out.append(healthText)
        out.append(Spacing.spacing(7 - healthOffset))
        return out.build()
    }
}