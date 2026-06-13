package dev.byrt.survivalgames.loot

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.map.MapDataPointType
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.player.PlayerVisuals
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.extension.name
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

@Suppress("unstableApiUsage")
object SGLoot {
    fun populateMapLoot(world: World, map: SGMap) {
        logger.info("Started populating loot for map '${map.mapName}'")
        if(map.lootChests.isEmpty()) {
            logger.info("No possible loot locations in map '${map.mapName}'")
        } else {
            map.lootChests.forEach { point ->
                spawnLootChest(world, point.x.toInt(), point.y.toInt(), point.z.toInt(),
                    when (point.mapDataPointType) {
                        MapDataPointType.LOOT_CHEST_1 -> SGLootPool.LOOT_CHEST_1
                        MapDataPointType.LOOT_CHEST_2 -> SGLootPool.LOOT_CHEST_2
                        MapDataPointType.LOOT_CHEST_3 -> SGLootPool.LOOT_CHEST_3
                        else -> SGLootPool.LOOT_CHEST_1
                    }
                )
            }
            logger.info("Finished populating loot for map '${map.mapName}'")
        }
    }

    fun spawnLootChest(world: World, x: Int, y: Int, z: Int, lootPool: SGLootPool) {
        val chest = world.getBlockAt(x, y, z).apply { type = Material.CHEST }
        val chestState = chest.state as Chest
        val inventory = chestState.inventory

        repeat(Random.nextInt(3, 6)) {
            val lootItem = SGLootPool.getRandomItem(lootPool)
            if (lootItem.material == Material.AIR) return
            if (lootItem.amountMin < 1) return
            if (lootItem.amountMax < 1) return

            val slotIndex = Random.nextInt(0, inventory.size - 1)
            val item = ItemStack(lootItem.material, Random.nextInt(lootItem.amountMin, lootItem.amountMax.inc())).apply {
                editMeta {
                    it.displayName(Formatting.allTags.deserialize("<reset><!i><${lootItem.rarity.rarityColour}>${SG_FONT_TAG}${lootItem.material.name()}</reset>"))
                    it.lore(listOf(Formatting.allTags.deserialize("<reset><!i><white>${lootItem.rarity.asMiniMessage()}${lootItem.type.asMiniMessage()}")))
                    it.isUnbreakable = true
                    it.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
                    if(lootItem.enchantments.enchantments().isNotEmpty()) {
                        lootItem.enchantments.enchantments().forEach { (enchantment, level) ->
                            addEnchantment(enchantment, level)
                        }
                    }
                }
            }
            if (inventory.getItem(slotIndex) == null) {
                inventory.setItem(slotIndex, item)
            } else {
                inventory.setItem(inventory.firstEmpty(), item)
            }
        }
    }

    fun spawnSupplyDrop(container: GameContainer?, map: SGMap) {
        val world = container?.containerWorld
        val supplyDropLocation = map.supplyDropSpawns
            .flatMap { point -> listOf(Location(world, point.x, point.y, point.z)) }
            .filter { dropLocation -> container?.containerWorld?.worldBorder?.isInside(dropLocation) == true }
            .randomOrNull()

        if(supplyDropLocation != null) {
            container?.players?.forEach { player ->
                player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<b><playercolour>${SG_FONT_TAG}Supply Drop spawning (<white>${supplyDropLocation.x.toInt()}, ${supplyDropLocation.y.toInt()}, ${supplyDropLocation.z.toInt()}</white><playercolour>)."))
                player.playSound(Sounds.Alert.SUPPLY_DROP_SPAWN)
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
                var height = supplyDropLocation.clone().y + 180
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
                            PlayerVisuals.shrinkBorder(container)
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
        } else return
    }
}