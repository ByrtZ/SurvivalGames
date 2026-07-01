package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.loot.SGLoot
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.music.JukeboxTrack
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.player.PlayerVisuals
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.attribute.Attribute
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
                            player.bukkitPlayer().showTitle(
                                Title.title(
                                    Formatting.glyph("\uD000"),
                                    Component.empty(),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(1),
                                        Duration.ofSeconds(1)
                                    )
                                )
                            )
                            player.bukkitPlayer().playSound(Sounds.Misc.TITLE_SCREEN_ENTER)
                        }
                    }
                    if (instance.timer.getTimer() == 28) {
                        for (player in instance.currentContainer?.players!!) {
                            player.bukkitPlayer().showTitle(
                                Title.title(
                                    Formatting.allTags.deserialize("<playercolour>${SG_FONT_TAG}Survival Games"),
                                    Component.empty(),
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
                            Jukebox.startMusicLoop(player.bukkitPlayer(), JukeboxTrack.IN_GAME)
                        }
                    }
                    if (instance.timer.getTimer() == 15) {
                        for (player in instance.currentContainer?.players!!) {
                            player.bukkitPlayer().playSound(player.bukkitPlayer().location, Sounds.Tutorial.TUTORIAL_POP, 1f, 1f)
                            player.bukkitPlayer().sendMessage(
                                Component.text("-----------------------------------------------------")
                                    .color(NamedTextColor.GREEN).decoration(TextDecoration.STRIKETHROUGH, true).append(
                                        Component.text(" Starting soon:\n\n").color(NamedTextColor.WHITE)
                                            .decoration(TextDecoration.BOLD, true)
                                            .decoration(TextDecoration.STRIKETHROUGH, false).append(
                                                Component.text("      When she survival on my game.\n\n")
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
                        }
                    }
                    if (instance.timer.getTimer() in 1..10) {
                        for (player in instance.currentContainer?.players!!) {
                            player.bukkitPlayer().showTitle(
                                Title.title(
                                    Formatting.allTags.deserialize("$SG_FONT_TAG<playercolour>Starting in"),
                                    Formatting.allTags.deserialize("<b>${if(instance.timer.getTimer() == 3) "<green>" else if(instance.timer.getTimer() == 2) "<yellow>" else if(instance.timer.getTimer() == 1) "<red>" else ""}►${instance.timer.getTimer()}◄"),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(5),
                                        Duration.ofSeconds(0)
                                    )
                                )
                            )
                            if(instance.timer.getTimer() in 1..3) player.bukkitPlayer().playSound(Sounds.Timer.STARTING_123)
                            player.bukkitPlayer().playSound(Sounds.Timer.STARTING_TICK)
                        }
                    }
                    if (instance.timer.getTimer() <= 0) {
                        instance.manager.nextState()
                    }
                }

                /** IN GAME **/
                if (instance.manager.getGameState() == GameState.IN_GAME && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    if(instance.manager.isGracePeriod == true) {
                        if(instance.timer.getTimer() <=
                            if(instance.manager.map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH - GameTime.GRACE_PERIOD
                            else GameTime.IN_GAME_TIME - GameTime.GRACE_PERIOD) {
                            instance.manager.isGracePeriod = false
                        }
                    }
                    if (instance.timer.getTimer() in 11..59 || instance.timer.getTimer() % 60 == 0) {
                        for (player in instance.currentContainer?.players!!) {
                            player.bukkitPlayer().playSound(Sounds.Timer.CLOCK_TICK)
                        }
                    }
                    if (instance.timer.getTimer() % 60 == 0) {
                        if(instance.timer.getTimer() == (if(instance.manager.map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH - 60 else GameTime.IN_GAME_TIME - 60) && instance.currentContainer?.containerWorld?.worldBorder?.size!! >= instance.manager.map.borderSize) {
                            PlayerVisuals.shrinkBorder(instance.currentContainer)
                        }
                    }
                    // Supply drop spawning
                    if (instance.timer.getTimer() < (if(instance.manager.map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH else GameTime.IN_GAME_TIME) && instance.timer.getTimer() % 75 == 0) {
                        SGLoot.spawnSupplyDrop(instance.currentContainer, instance.manager.map)
                    }
                    if (instance.timer.getTimer() in 0..10) {
                        for (player in instance.currentContainer?.players!!) {
                            player.bukkitPlayer().playSound(Sounds.Timer.CLOCK_TICK_HIGH)
                        }
                    }
                    if (instance.timer.getTimer() <= 0) {
                        instance.manager.nextState()
                    }
                }

                /** OVERTIME **/
                if (instance.manager.getGameState() == GameState.OVERTIME && instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                    if (instance.timer.getTimer() % 15 == 0) {
                        for (player in instance.currentContainer?.players!!.filter { sgPlayer -> sgPlayer.playerType == PlayerType.PARTICIPANT }) {
                            if (player.bukkitPlayer().getAttribute(Attribute.MAX_HEALTH)?.value != null) {
                                val maxHealth = player.bukkitPlayer().getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
                                if(maxHealth > 2.0) {
                                    player.bukkitPlayer().getAttribute(Attribute.MAX_HEALTH)?.baseValue -= 2.0
                                    player.bukkitPlayer().damage(0.01)
                                    player.bukkitPlayer().playSound(Sounds.Alert.HEALTH_DECREASE)
                                }
                            }
                        }
                    }
                    if (instance.timer.getTimer() <= (GameTime.OVERTIME_TIME - 60) && instance.currentContainer?.containerWorld?.worldBorder?.size!! >= 50.0) {
                        PlayerVisuals.shrinkBorder(instance.currentContainer, newSize = 20.0, overrideTicks = 20L * 12L)
                    }
                    if (instance.timer.getTimer() <= (GameTime.OVERTIME_TIME - 80) && instance.currentContainer?.containerWorld?.worldBorder?.size!! >= 20.0) {
                        PlayerVisuals.shrinkBorder(instance.currentContainer, newSize = 10.0, overrideTicks = 20L * 8L)
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
                    if (instance.timer.getTimer() == 0) {
                        for (player in instance.currentContainer?.players!!) {
                            player.bukkitPlayer().showTitle(
                                Title.title(
                                    Formatting.glyph("\uD000"),
                                    Component.empty(),
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