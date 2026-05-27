package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.music.MusicTrack
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration

class GameInstanceTask(val instance: GameInstance) {
    private var gameRunnables = mutableMapOf<Int, BukkitRunnable>()
    private var currentGameTaskId = 0
    fun startGameLoop() {
        val gameRunnable = object : BukkitRunnable() {
            override fun run() {
                instance.info.updateGameTimer()
                /** STARTING **/
                if (instance.manager.getGameState() == GameState.STARTING && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    if (instance.timer.getTimer() == 30) {
                        for (player in instance.currentContainer?.players!!) {
                            player.showTitle(
                                Title.title(
                                    Formatting.glyph("\uD000"),
                                    Component.text(""),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(1),
                                        Duration.ofSeconds(1)
                                    )
                                )
                            )
                            player.playSound(Sounds.Misc.TITLE_SCREEN_ENTER)
                        }
                    }
                    if (instance.timer.getTimer() == 28) {
                        for (player in instance.currentContainer?.players!!) {
                            player.showTitle(
                                Title.title(
                                    Formatting.allTags.deserialize("<playercolour>${SG_FONT_TAG}Survival Games"),
                                    Component.text(""),
                                    Title.Times.times(
                                        Duration.ofSeconds(1),
                                        Duration.ofSeconds(4),
                                        Duration.ofSeconds(1)
                                    )
                                )
                            )
                        }
                    }
                    if (instance.timer.getTimer() == 26) {
                        for (player in instance.currentContainer?.players!!) {
                            player.playSound(player.location, Sounds.Tutorial.TUTORIAL_POP, 1f, 1f)
                            player.sendMessage(
                                Component.text("-----------------------------------------------------")
                                .color(NamedTextColor.GREEN).decoration(TextDecoration.STRIKETHROUGH, true).append(
                                    Component.text(" Starting soon:\n\n").color(NamedTextColor.WHITE)
                                    .decoration(TextDecoration.BOLD, true)
                                    .decoration(TextDecoration.STRIKETHROUGH, false).append(
                                    Component.text("      I don't have anything funny to say, this just needs replacing.\n\n")
                                        .color(NamedTextColor.RED).decoration(TextDecoration.BOLD, false)
                                        .decoration(TextDecoration.ITALIC, true)
                                        .append(Component.text("\n\n\n").append(
                                                Component.text("-----------------------------------------------------")
                                                    .color(NamedTextColor.GREEN)
                                                    .decoration(TextDecoration.STRIKETHROUGH, true)
                                                    .decoration(TextDecoration.ITALIC, false)
                                            )
                                        )
                                    )
                                )
                            )
                            Jukebox.startMusicLoop(player, MusicTrack.IN_GAME)
                        }
                    }
                    if (instance.timer.getTimer() in 4..10) {
                        for (player in instance.currentContainer?.players!!) {
                            player.showTitle(
                                Title.title(
                                    Component.text("Starting in").color(NamedTextColor.AQUA),
                                    Component.text("►${instance.timer.getTimer()}◄").decoration(TextDecoration.BOLD, true),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(5),
                                        Duration.ofSeconds(0)
                                    )
                                )
                            )
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                        }
                    }
                    if (instance.timer.getTimer() in 1..3) {
                        for (player in instance.currentContainer?.players!!) {
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                            player.playSound(Sounds.Timer.STARTING_123)
                            if (instance.timer.getTimer() == 3) {
                                player.showTitle(
                                    Title.title(
                                        Component.text("Starting in").color(NamedTextColor.AQUA),
                                        Component.text("►${instance.timer.getTimer()}◄").color(NamedTextColor.GREEN)
                                            .decoration(TextDecoration.BOLD, true),
                                        Title.Times.times(
                                            Duration.ofSeconds(0),
                                            Duration.ofSeconds(5),
                                            Duration.ofSeconds(0)
                                        )
                                    )
                                )
                            }
                            if (instance.timer.getTimer() == 2) {
                                player.showTitle(
                                    Title.title(
                                        Component.text("Starting in").color(NamedTextColor.AQUA),
                                        Component.text("►${instance.timer.getTimer()}◄").color(NamedTextColor.YELLOW)
                                            .decoration(TextDecoration.BOLD, true),
                                        Title.Times.times(
                                            Duration.ofSeconds(0),
                                            Duration.ofSeconds(5),
                                            Duration.ofSeconds(0)
                                        )
                                    )
                                )
                            }
                            if (instance.timer.getTimer() == 1) {
                                player.showTitle(
                                    Title.title(
                                        Component.text("Starting in").color(NamedTextColor.AQUA),
                                        Component.text("►${instance.timer.getTimer()}◄").color(NamedTextColor.RED)
                                            .decoration(TextDecoration.BOLD, true),
                                        Title.Times.times(
                                            Duration.ofSeconds(0),
                                            Duration.ofSeconds(5),
                                            Duration.ofSeconds(0)
                                        )
                                    )
                                )
                            }
                        }
                    }
                    if (instance.timer.getTimer() <= 0) {
                        instance.manager.nextState()
                    }
                }

                /** IN GAME **/
                if (instance.manager.getGameState() == GameState.IN_GAME && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    if (instance.timer.getTimer() in 11..59 || instance.timer.getTimer() % 60 == 0) {
                        for (player in instance.currentContainer?.players!!) {
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                        }
                    }
                    if (instance.timer.getTimer() % 60 == 0) {
                        //TODO faster border movements but move a few times
                        if(instance.timer.getTimer() <= GameTime.IN_GAME_TIME - 60 && instance.currentContainer?.containerWorld?.worldBorder?.size!! >= 750.0) {
                            instance.currentContainer?.containerWorld?.worldBorder?.changeSize(50.0, instance.timer.getTimer().times(20).toLong())
                            for (player in instance.currentContainer?.players!!) {
                                player.playSound(Sounds.Alert.ALARM)
                                player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333><b>${SG_FONT_TAG}The World Border is now shrinking!"))
                                player.showTitle(
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
                    }
                    if (instance.timer.getTimer() in 0..10) {
                        for (player in instance.currentContainer?.players!!) {
                            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
                        }
                    }
                    if (instance.timer.getTimer() <= 0) {
                        instance.manager.nextState()
                    }
                }

                /** OVERTIME **/
                if (instance.manager.getGameState() == GameState.OVERTIME && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    if (instance.timer.getTimer() in 11..30 || instance.timer.getTimer() % 60 == 0) {
                        for (player in instance.currentContainer?.players!!) {
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                        }
                    }
                    if (instance.timer.getTimer() in 0..10) {
                        for (player in instance.currentContainer?.players!!) {
                            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
                        }
                    }
                    if (instance.timer.getTimer() <= 0) {
                        instance.manager.nextState()
                    }
                }

                /** ROUND END **/
                if (instance.timer.getTimer() <= 0 && instance.manager.getGameState() == GameState.ROUND_END && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    instance.manager.nextState()
                }

                /** GAME END **/
                if (instance.manager.getGameState() == GameState.GAME_END && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    if (instance.timer.getTimer() == 1) {
                        for (player in instance.currentContainer?.players!!) {
                            player.showTitle(
                                Title.title(
                                    Formatting.glyph("\uD000"),
                                    Formatting.allTags.deserialize(""),
                                    Title.Times.times(
                                        Duration.ofMillis(250),
                                        Duration.ofSeconds(1),
                                        Duration.ofMillis(250)
                                    )
                                )
                            )
                        }
                        instance.manager.nextState()
                    }
                }

                /** TIMER DECREMENTS IF ACTIVE **/
                if (instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    instance.timer.decrement()
                }
            }
        }
        gameRunnable.runTaskTimer(plugin, 0L, 20L)
        currentGameTaskId = gameRunnable.taskId
        gameRunnables[gameRunnable.taskId] = gameRunnable
    }

    fun stopGameLoop() {
        gameRunnables.remove(currentGameTaskId)?.cancel()
    }
}