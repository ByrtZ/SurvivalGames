package dev.byrt.survivalgames.loot

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.map.MapDataPointType
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.player.PlayerVisuals
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.itemStack
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

object SGLoot {
    fun populateMapLoot(world: World, map: SGMap) {
        logger.info("Started populating loot for map '${map.mapName}'")
        if(map.lootChests.isEmpty()) {
            logger.info("No possible loot locations in map '${map.mapName}'")
        } else {
            map.lootChests.forEach { point ->
                spawnLootChest(world, point.x.toInt(), point.y.toInt(), point.z.toInt(), if(point.mapDataPointType == MapDataPointType.LOOT_CHEST_1) 1 else if(point.mapDataPointType == MapDataPointType.LOOT_CHEST_2) 2 else if(point.mapDataPointType == MapDataPointType.LOOT_CHEST_3) 3 else 1)
            }
            logger.info("Finished populating loot for map '${map.mapName}'")
        }
    }

    fun spawnLootChest(world: World, x: Int, y: Int, z: Int, tier: Int = 1) {
        val chest = world.getBlockAt(x, y, z).apply { type = Material.CHEST }
        val chestState = chest.state as Chest
        val inventory = chestState.inventory
        val lootTable = when(tier) {
            1 -> SGLootTable.LOOT_CHEST_1
            2 -> SGLootTable.LOOT_CHEST_2
            3 -> SGLootTable.LOOT_CHEST_3
            4 -> SGLootTable.SUPPLY_DROP
            else -> SGLootTable.LOOT_CHEST_1
        }
        val amountLootPicks = Random.nextInt(2, 5)
        for(i in 1..amountLootPicks) {
            val randomSlotIndex = Random.nextInt(0, inventory.size - 1)
            val randomLoot = lootTable.possibleLoot.toList().random()
            val randomLootMaterial = randomLoot.first
            val randomLootAmount = randomLoot.second.random()
            val itemStack = ItemStack(randomLootMaterial, randomLootAmount).apply {
                this.itemMeta.apply {
                    isUnbreakable = true
                    addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
                }
            }
            if(inventory.getItem(randomSlotIndex) == null) {
                inventory.setItem(randomSlotIndex, itemStack)
            } else {
                inventory.setItem(inventory.firstEmpty(), itemStack)
            }
        }
    }

    fun spawnSupplyDrop(container: GameContainer?, map: SGMap) {
        val world = container?.containerWorld
        val supplyDropLocation = map.supplyDropSpawns.flatMap { point -> listOf(Location(world, point.x, point.y, point.z)) }.random()
        container?.players?.forEach { player -> player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<b><playercolour>${SG_FONT_TAG}Supply Drop spawning</playercolour> (<white>${supplyDropLocation.x}, ${supplyDropLocation.y}, ${supplyDropLocation.z}</white><playercolour>).")) }
        // Spawn beacon location
        for(x in -1..1) {
            for(y in -2..-1) {
                for(z in -1..1) {
                    world?.getBlockAt(supplyDropLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble()))?.type = Material.IRON_BLOCK
                }
            }
        }
        world?.getBlockAt(supplyDropLocation.clone().subtract(0.0, 1.0, 0.0))?.type = Material.BEACON
        // Runnable for fireworks
        object : BukkitRunnable() {
            var height = supplyDropLocation.clone().y + 180
            override fun run() {
                if(container?.instance?.manager?.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    if(height <= supplyDropLocation.clone().y.toInt()) {
                        world?.let { spawnLootChest(it, supplyDropLocation.x.toInt(), supplyDropLocation.y.toInt(), supplyDropLocation.z.toInt()) }
                        PlayerVisuals.firework(
                            Location(world, supplyDropLocation.clone().x + 0.5, supplyDropLocation.y, supplyDropLocation.clone().z + 0.5),
                            flicker = false,
                            trail = false,
                            color = Color.YELLOW,
                            fireworkType = FireworkEffect.Type.BALL,
                            variedVelocity = false
                        )
                        world?.getBlockAt(supplyDropLocation.clone().subtract(0.0, 1.0, 0.0))?.type = Material.OBSIDIAN
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
        }.runTaskTimer(plugin, 20L * 15L, 4L)
    }
}