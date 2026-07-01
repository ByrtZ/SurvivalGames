package dev.byrt.survivalgames.nametag

import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f
import java.lang.AutoCloseable

class NameTag(
    player: Player,
    val lines: Int
) : AutoCloseable {

    companion object {
        private const val BASE_HEIGHT = 0.25f
        private const val LINE_HEIGHT = 0.3f
        private const val VIEW_RANGE = 32f
        private const val SHOW_TO_SELF = false
    }

    private val entities = MutableList(lines) { index ->
        player.world.spawn(player.location, TextDisplay::class.java).apply {
            isPersistent = false
            billboard = Display.Billboard.CENTER
            isShadowed = true
            backgroundColor = Color.fromARGB(1073741824)
            transformation = Transformation(
                Vector3f(
                    0f,
                    BASE_HEIGHT + LINE_HEIGHT * (lines - index - 1),
                    0f
                ),
                Quaternionf(),
                Vector3f(1f),
                Quaternionf()
            )

            viewRange = VIEW_RANGE / 80f

            player.addPassenger(this)

            if (!SHOW_TO_SELF) {
                player.hideEntity(plugin, this)
            }
        }
    }

    operator fun get(index: Int): TextDisplay = entities[index]

    operator fun set(index: Int, component: Component) = entities[index].text(component)

    override fun close() = entities.forEach(Entity::remove)
}

abstract class NameTagProvider {
    /**
     * Number of lines this provider requires.
     */
    abstract val lines: Int

    /**
     * Called after being assigned.
     */
    open fun initialise(player: SGPlayer) {}

    /**
     * Refresh the player's nametag.
     */
    abstract fun update(player: SGPlayer)

    /**
     * Called before being replaced.
     */
    open fun destroy(player: SGPlayer) {}
}