package dev.byrt.survivalgames.game.mechanic.drop

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.game.visuals.GameVisuals
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.loot.SGLoot.spawnLootChest
import dev.byrt.survivalgames.loot.pool.SGLootPool
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.player.visuals.PlayerVisuals
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import kotlin.collections.contains
import kotlin.collections.set

object SGSupplyDrop {
    fun spawn(container: GameContainer?, map: SGMap) {
        val world = container?.containerWorld
        val supplyDropLocation = map.supplyDropSpawns
            .flatMap { point -> listOf(Location(world, point.x, point.y, point.z)) }
            .filter { dropLocation -> container?.containerWorld?.worldBorder?.isInside(dropLocation) == true && dropLocation.block.type != Material.CHEST}
            .randomOrNull()

        if(supplyDropLocation != null) {
            // Track active location
            val supplyDropID = UUID.randomUUID()
            container?.instance?.manager?.activeSupplyDrops[supplyDropID] = supplyDropLocation
            container?.players?.forEach { player ->
                player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<b><playercolour>${SG_FONT_TAG}Supply Drop spawning (<white>${player.bukkitPlayer().location.distance(supplyDropLocation).toInt()} blocks away</white><playercolour>)."))
                player.bukkitPlayer().playSound(Sounds.Alert.SUPPLY_DROP_SPAWN)
                player.bukkitPlayer().compassTarget = supplyDropLocation
            }
            // Spawn beacon location
            for(x in -1..1) {
                for(y in -2..-1) {
                    for(z in -1..1) {
                        world?.getBlockAt(supplyDropLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble()))?.type = Material.IRON_BLOCK
                    }
                }
            }
            world?.getBlockAt(supplyDropLocation.clone().subtract(0.0, 1.0, 0.0))?.type = Material.BEACON
            // Pause border until drop completes descent
            container?.containerWorld?.worldBorder?.size = container.containerWorld.worldBorder.size
            // Runnable for fireworks
            object : BukkitRunnable() {
                var height = supplyDropLocation.clone().y + 200
                override fun run() {
                    if(container?.instance?.manager?.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                        if(height <= supplyDropLocation.clone().y.toInt()) {
                            world?.let { spawnLootChest(it, supplyDropLocation.x.toInt(), supplyDropLocation.y.toInt(), supplyDropLocation.z.toInt(), SGLootPool.SUPPLY_DROP) }
                            PlayerVisuals.firework(
                                Location(world, supplyDropLocation.clone().x + 0.5, supplyDropLocation.y, supplyDropLocation.clone().z + 0.5),
                                flicker = true,
                                trail = true,
                                color = Color.ORANGE,
                                fireworkType = FireworkEffect.Type.BALL_LARGE,
                                variedVelocity = false
                            )
                            world?.getBlockAt(supplyDropLocation.clone().subtract(0.0, 1.0, 0.0))?.type = Material.OBSIDIAN
                            // Short delay on shrinking again
                            object : BukkitRunnable() {
                                override fun run() {
                                    if(container?.instance?.manager?.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                                        container?.instance?.manager?.activeSupplyDrops?.remove(supplyDropID)
                                        GameVisuals.shrinkBorder(container)
                                    }
                                }
                            }.runTaskLater(plugin, 20L * 8L)
                            cancel()
                        } else {
                            PlayerVisuals.firework(
                                Location(world, supplyDropLocation.clone().x + 0.5, height, supplyDropLocation.clone().z + 0.5),
                                flicker = false,
                                trail = false,
                                color = Color.YELLOW,
                                fireworkType = FireworkEffect.Type.BURST,
                                variedVelocity = false
                            )
                            height -= 2
                        }
                    } else {
                        cancel()
                    }
                }
            }.runTaskTimer(plugin, 20L * 18L, 4L)
        } else return
    }
}