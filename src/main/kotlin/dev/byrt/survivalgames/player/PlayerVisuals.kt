package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.interfaces.SGDeathInterface
import dev.byrt.survivalgames.item.SGItem
import dev.byrt.survivalgames.library.SGDeathMessages
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.lobby.info.LobbyInfo
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import kotlin.random.Random

object PlayerVisuals {
    fun damageIndicator(entity: LivingEntity, damage: Double) {
        if(damage > 1000) return
        val damageTaken = BigDecimal(damage).setScale(2, RoundingMode.HALF_EVEN)
        val damageIndicatorEntity = entity.location.world.spawn(entity.location.clone().add(Random.nextDouble(-0.35, 0.35), Random.nextDouble(0.5, 2.5), Random.nextDouble(-0.35, 0.35)), TextDisplay::class.java).apply {
            alignment = TextDisplay.TextAlignment.CENTER
            billboard = Display.Billboard.VERTICAL
            isShadowed = true
            isCustomNameVisible = true
            scoreboardTags.add("sg.damage_indicator")
            customName(Formatting.allTags.deserialize("${SG_FONT_TAG}${if(damageTaken.toInt() <= 4) "<yellow>" else if(damageTaken.toInt() in 5..7) "<gold>" else if(damageTaken.toInt() >= 8) "<red>" else "<#000000>"}${damageTaken}"))
        }
        object : BukkitRunnable() {
            override fun run() {
                damageIndicatorEntity.remove()
            }
        }.runTaskLater(plugin, 30L)
    }

    /**
     * @param [player] Player to die
     * @param [killer] Player who killed the [player], nullable
     * @param [showDeathMessage] Should the death message be shown
     */
    fun death(player: Player, killer: Player?, showDeathMessage: Boolean) {
        player.sgPlayer().isDead = true
        player.sgPlayer().setType(PlayerType.SPECTATOR)

        val deathMessage = if (showDeathMessage) {
            if (killer != null) {
                Formatting.allTags.deserialize("[<#ff3333><unicodeprefix:skull></#ff3333>] ").append(Formatting.allTags.deserialize("$SG_FONT_TAG${SGDeathMessages.killerMessages.random().replace("%s", "<#ff3333>${player.name}</#ff3333>").replace("%k", "<#ff3333>${killer.name}</#ff3333>")}"))
            } else {
                Formatting.allTags.deserialize("[<#ff3333><unicodeprefix:skull></#ff3333>] ").append(Formatting.allTags.deserialize("$SG_FONT_TAG${SGDeathMessages.nonKillerMessages.random().replace("%s", "<#ff3333>${player.name}</#ff3333>")}"))
            }
        } else Component.empty()
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 50, 255, true, false))
        player.showTitle(
            Title.title(
                Formatting.allTags.deserialize("<#ff3333>${SG_FONT_TAG}You died!"),
                deathMessage,
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(8),
                    Duration.ofMillis(750)
                )
            )
        )
        player.playSound(Sounds.Score.DEATH)
        player.playSound(Sounds.Score.DEATH_BACKGROUND)
        deathMessage.let(Bukkit::broadcast)
        repeat(3) {
            firework(
                player.location,
                flicker = false,
                trail = false,
                color = Color.RED,
                fireworkType = FireworkEffect.Type.BALL_LARGE,
                variedVelocity = true
            )
        }

        val inventoryContents = player.inventory.storageContents + player.inventory.armorContents
        inventoryContents.forEach { item ->
            player.world.spawn(player.location, Item::class.java).apply {
                if (item != null && item.type !in listOf(Material.AIR, Material.COMPASS, Material.LANTERN)) {
                    itemStack = item
                    velocity = Vector(Random.nextDouble(-0.25, 0.25), Random.nextDouble(-0.25, 0.25), Random.nextDouble(-0.25, 0.25))
                } else {
                    remove()
                }
            }
        }
        player.inventory.clear()

        val deathOverlayItem = ItemStack(Material.CARVED_PUMPKIN)
        val deathOverlayItemMeta = deathOverlayItem.itemMeta
        deathOverlayItem.addEnchantment(Enchantment.BINDING_CURSE, 1)
        deathOverlayItemMeta.itemModel = NamespacedKey("minecraft", "air")
        deathOverlayItem.itemMeta = deathOverlayItemMeta
        player.inventory.helmet = deathOverlayItem
        val deathVehicle = player.world.spawn(player.location.add(0.0, 0.6, 0.0), ItemDisplay::class.java).apply {
            teleportDuration = 1
            addScoreboardTag("${player.uniqueId}-death-vehicle")
            addPassenger(player)
        }
        player.sendActionBar(Component.empty())

        hidePlayer(player)

        player.sgPlayer().currentContainer?.instance?.manager?.gameEndCheck()

        /** Move death vehicle ahead of the killer as a faux spectator mode. **/
        if(killer != null) {
            object : BukkitRunnable() {
                override fun run() {
                    player.playSound(Sounds.Score.DEATH_STATS)
                    object : BukkitRunnable() {
                        override fun run() {
                            if(killer.sgPlayer().isDead || deathVehicle.isDead || !deathVehicle.passengers.contains(player) || !player.isOnline) {
                                this.cancel()
                            } else {
                                if(!deathVehicle.passengers.contains(player)) deathVehicle.addPassenger(player)
                                val killerLocation = killer.location.setRotation(killer.yaw, 0f)
                                val killerDirection = killerLocation.add(killerLocation.direction.normalize().multiply(2))
                                killerDirection.y = killer.location.y + 0.25
                                deathVehicle.teleport(killerDirection)
                                player.setRotation(killer.yaw - 180.0f, 5.0f)
                            }

                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
            }.runTaskLater(plugin, 50L)
        }

        /** Respawn (delayed slightly to prevent accidental free-cam or hub returns), includes ability to return to hub, continue spectating, and Post Respawn **/
        object : BukkitRunnable() {
            override fun run() {
                if(player.vehicle == deathVehicle) {
                    if(player.sgPlayer().currentContainer?.instance?.manager?.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                        fullRespawn(player, deathVehicle)
                        cancel()
                    } else {
                        player.sendActionBar(Formatting.allTags.deserialize("${SG_FONT_TAG}Press <playercolour><key:key.sneak></playercolour> to continue"))
                        if(player.isSneaking && player.sgPlayer().currentContainer?.instance?.manager?.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                            player.gameMode = GameMode.ADVENTURE
                            defaultCoroutineScope.launch { SGDeathInterface.create(player) }
                            cancel()
                        }
                    }
                } else {
                    deathVehicle.remove()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 20L * 5L, 1L)
    }

    fun fullRespawn(player: Player, deathVehicle: ItemDisplay, shouldLeaveContainer: Boolean = false) {
        player.gameMode = GameMode.SPECTATOR
        player.sendActionBar(Component.empty())
        object : BukkitRunnable() {
            override fun run() {
                respawn(player)
                object : BukkitRunnable() {
                    override fun run() {
                        postRespawn(player, deathVehicle)
                        if(shouldLeaveContainer) GameManager.removePlayerFromContainer(player)
                    }
                }.runTaskLater(plugin, 20L)
            }
        }.runTask(plugin)
    }

    fun showPlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player)
        }
    }
    fun hidePlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.hidePlayer(plugin, player)
        }
    }

    fun respawn(player: Player) {
        player.showTitle(
            Title.title(
                Formatting.glyph("\uD000"),
                Formatting.allTags.deserialize(""),
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(2),
                    Duration.ofMillis(500)
                )
            )
        )
        player.playSound(Sounds.Score.RESPAWN)
    }

    fun postRespawn(player: Player, vehicle: ItemDisplay) {
        vehicle.eject()
        vehicle.remove()
        player.sgPlayer().isDead = false
        player.fireTicks = 0
        player.health = player.getAttribute(Attribute.MAX_HEALTH)!!.value
        player.foodLevel = 20
        player.inventory.helmet = null
        showPlayer(player)
    }

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

    /** Resets player gamemode, health, food and saturation by default.
     * Parameters available for the following:
     * Reset active bossbars [shouldClearBossBar],
     * Clear inventory [shouldClearInventory],
     * Reset scoreboard [shouldResetScoreboard]
     * **/
    fun resetPlayerState(player: Player, shouldClearBossBar: Boolean = false, shouldClearInventory: Boolean = false, shouldResetScoreboard: Boolean = false) {
        if(shouldClearBossBar) player.activeBossBars().forEach { bossBar -> bossBar.removeViewer(player) }
        if(shouldClearInventory) player.inventory.clear()
        if(shouldResetScoreboard) player.scoreboard = LobbyInfo.lobbyScoreboard
        if(player.sgPlayer().playerType == PlayerType.SPECTATOR) player.gameMode = GameMode.SPECTATOR else player.gameMode = GameMode.ADVENTURE
        player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = 20.0
        player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        player.foodLevel = 20
        player.saturation = 0f
    }

    /**
     * @param [location] Where to spawn the firework
     * @param [flicker] Whether the firework has the flicker effect
     * @param [trail] Whether the firework has the trail effect
     * @param [color] What colour the firework is
     * @param [fireworkType] What type the firework is
     * @param [variedVelocity] Whether the firework should shoot into the sky with a random offset
     */
    fun firework(
        location: Location,
        flicker: Boolean,
        trail: Boolean,
        color: Color,
        fireworkType: FireworkEffect.Type,
        variedVelocity: Boolean
    ) {
        val f: Firework = location.world.spawn(
            Location(location.world, location.x, location.y + 1.0, location.z),
            Firework::class.java
        )
        val fm = f.fireworkMeta
        fm.addEffect(
            FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(fireworkType)
                .withColor(color)
                .build()
        )
        if (variedVelocity) {
            fm.power = 1
            f.fireworkMeta = fm
            val direction = Vector(
                Random.nextDouble(-0.005, 0.005),
                Random.nextDouble(0.25, 0.35),
                Random.nextDouble(-0.005, 0.005)
            ).normalize()
            f.velocity = direction
        } else {
            fm.power = 0
            f.fireworkMeta = fm
            f.ticksToDetonate = 0
        }
    }
}