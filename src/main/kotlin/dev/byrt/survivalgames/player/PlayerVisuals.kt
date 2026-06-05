package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import kotlin.random.Random

object PlayerVisuals {
    fun damageIndicator(entity: LivingEntity, damage: Double) {
        val damageTaken = BigDecimal(damage).setScale(2, RoundingMode.HALF_EVEN)
        val damageIndicatorEntity = entity.location.world.spawn(entity.location.clone().add(Random.nextDouble(-0.25, 0.35), Random.nextDouble(0.5, 2.5), Random.nextDouble(-0.25, 0.35)), TextDisplay::class.java).apply {
            alignment = TextDisplay.TextAlignment.CENTER
            billboard = Display.Billboard.VERTICAL
            isShadowed = true
            isCustomNameVisible = true
            scoreboardTags.add("sg.damage_indicator")
            customName(Formatting.allTags.deserialize("${SG_FONT_TAG}${if(damageTaken.toInt() <= 3) "<yellow>" else if(damageTaken.toInt() in 4..6) "<gold>" else if(damageTaken.toInt() >= 7) "<red>" else "<#000000>"}${damageTaken}"))
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
        player.sgPlayer().currentContainer?.instance?.manager?.gameEndCheck()
        player.inventory.storageContents.forEach { item ->
            player.world.spawn(player.location, Item::class.java).apply {
                if (item != null && item.type != Material.AIR) {
                    itemStack = item
                    velocity = Vector(Random.nextDouble(0.05, 0.25), Random.nextDouble(0.05, 0.25), Random.nextDouble(0.05, 0.25))
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

        val deathMessage = if (showDeathMessage) {
            if (killer != null) {
                Component.translatable("sg.death.killed_by", player.displayName(), killer.displayName())
            } else {
                Component.translatable("sg.death.died", player.displayName())
            }
        } else Component.empty()

        player.showTitle(
            Title.title(
                Formatting.allTags.deserialize("<#ff3333>You died!"),
                deathMessage,
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(8),
                    Duration.ofMillis(750)
                )
            )
        )
        player.playSound(Sounds.Score.DEATH)
        deathMessage.let(Bukkit::broadcast)
        for(i in 0..2) {
            firework(
                player.location,
                flicker = false,
                trail = false,
                color = Color.RED,
                fireworkType = FireworkEffect.Type.BALL_LARGE,
                variedVelocity = true
            )
        }

        hidePlayer(player)

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
                                deathVehicle.teleport(killerDirection, TeleportFlag.EntityState.RETAIN_PASSENGERS)
                                player.setRotation(killer.yaw - 180.0f, 5.0f)
                            }

                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
            }.runTaskLater(plugin, 50L)
        }

        /** Respawn, includes ability to return to hub, continue spectating, and Post Respawn **/
        //TODO: rework to allow continuous spectating and returning to hub
        object : BukkitRunnable() {
            val RESPAWN_TIME = 15
            var timer = RESPAWN_TIME
            var ticks = 0
            override fun run() {
                if(player.vehicle == deathVehicle) {
                    if(player.sgPlayer().currentContainer?.instance?.manager?.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) timer = 0
                    if(timer <= 0) {
                        object : BukkitRunnable() {
                            override fun run() {
                                respawn(player)
                                object : BukkitRunnable() {
                                    override fun run() {
                                        postRespawn(player, deathVehicle)
                                    }
                                }.runTaskLater(plugin, 20L)
                            }
                        }.runTask(plugin)
                        cancel()
                    } else {
                        if(ticks >= 20) {
                            ticks = 0
                            timer--
                        }
                        ticks++
                    }
                } else {
                    deathVehicle.remove()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
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
        //SpawnPoints.respawnLocation(player)
        //ItemManager.givePlayerTeamBoots(player)
        showPlayer(player)
    }

    fun shrinkBorder(container: GameContainer?) {
        container?.containerWorld?.worldBorder?.changeSize(50.0, container.instance.timer.getTimer().times(20).toLong())
        for (player in container?.instance?.currentContainer?.players!!) {
            player.playSound(Sounds.Alert.ALARM)
            player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333><b>${SG_FONT_TAG}The World Border is shrinking!"))
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

    fun gracePeriodEnd(container: GameContainer?) {
        for (player in container?.instance?.currentContainer?.players!!) {
            player.playSound(Sounds.Alert.ALARM)
            player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333><b>${SG_FONT_TAG}Grace Period has ended."))
            player.showTitle(
                Title.title(
                    Component.empty(),
                    Formatting.allTags.deserialize("<#ff3333><b>${SG_FONT_TAG}Grace Period ended."),
                    Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(250)
                    )
                )
            )
        }
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