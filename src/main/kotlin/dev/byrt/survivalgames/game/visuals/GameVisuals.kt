package dev.byrt.survivalgames.game.visuals

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.item.SGItem
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.time.Duration

object GameVisuals {
    fun shrinkBorder(container: GameContainer?, newSize: Double = 50.0, overrideTicks: Long = 0) {
        if(newSize == container?.containerWorld?.worldBorder?.size) return
        // Prevent shrink if supply drops active
        if(container?.instance?.manager?.activeSupplyDrops?.isNotEmpty() == true) return
        container?.containerWorld?.worldBorder?.changeSize(newSize, if(overrideTicks > 0) overrideTicks else container.instance.timer.getTimer().times(20).toLong())
        for(player in container?.instance?.currentContainer?.players!!) {
            player.bukkitPlayer().playSound(Sounds.Alert.ALARM)
            player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333><b>${SG_FONT_TAG}The World Border is shrinking!"))
            player.bukkitPlayer().showTitle(
                Title.title(
                    Component.empty(),
                    Formatting.allTags.deserialize("<#ff3333><b>${SG_FONT_TAG}World Border shrinking!"),
                    Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(250)
                    )
                )
            )
        }
    }

    fun gracePeriodStart(container: GameContainer?) {
        for(player in container?.instance?.currentContainer?.players!!) {
            player.bukkitPlayer().playSound(Sounds.Alert.GRACE_PERIOD_BEGIN)
            player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#20d600><b>${SG_FONT_TAG}Grace Period has begun."))
            player.bukkitPlayer().showTitle(
                Title.title(
                    Component.empty(),
                    Formatting.allTags.deserialize("<#20d600><b>${SG_FONT_TAG}Grace Period active!"),
                    Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(250)
                    )
                )
            )
        }
    }

    fun gracePeriodEnd(container: GameContainer?) {
        for(player in container?.instance?.currentContainer?.players!!) {
            player.bukkitPlayer().playSound(Sounds.Alert.GRACE_PERIOD_END)
            player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333><b>${SG_FONT_TAG}Grace Period has ended."))
            player.bukkitPlayer().showTitle(
                Title.title(
                    Component.empty(),
                    Formatting.allTags.deserialize("<#ff3333><b>${SG_FONT_TAG}Grace Period ended!"),
                    Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(250)
                    )
                )
            )
            player.bukkitPlayer().give(SGItem.getSupplyDropCompass())
        }
    }
}