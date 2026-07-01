package dev.byrt.survivalgames.world

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.ioCoroutineScope
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.plugin
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object SGWorld: Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    suspend fun createNewGameWorld(worldId: UUID): World =
        suspendCancellableCoroutine { cont ->
            ioCoroutineScope.launch {
                try {
                    val worldName = "sg-game-$worldId"
                    val worldFolder = File(Bukkit.getWorldContainer(), worldName)
                    val regionSource = File(plugin.dataFolder, "worlds/master/region")
                    val regionTarget = File(worldFolder, "region")
                    if (worldFolder.exists()) worldFolder.deleteRecursively()

                    regionTarget.parentFile.mkdirs()

                    regionSource.copyRecursively(
                        target = regionTarget,
                        overwrite = true
                    )

                    object : BukkitRunnable() {
                        override fun run() {
                            try {
                                Bukkit.getWorld(worldName)?.let { Bukkit.unloadWorld(it, false) }
                                val world = getWorldCreator(worldId).createWorld() ?: error("Failed to create world")
                                cont.resume(world)

                            } catch (e: Exception) {
                                cont.resumeWithException(e)
                            }
                        }
                    }.runTask(plugin)

                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            }
        }

    fun deleteGameWorld(world: World) {
        if (world == Bukkit.getWorlds()[0]) return
        val path = world.worldPath
        object : BukkitRunnable() {
            var unloaded = false
            override fun run() {
                if(world.players.isNotEmpty()) { // Dispatch players from game world
                    world.players.toList().forEach { player ->
                        GameManager.removePlayerFromContainer(player)
                    }
                    return
                }
                if(!unloaded) { // Unload game world when no players are in it
                    unloaded = Bukkit.unloadWorld(world, false)
                    if (!unloaded) {
                        logger.warning("Failed to unload game world '${world.name}'")
                        return
                    }
                    return
                }
                if(Bukkit.getWorld(world.name) != null) { // Returns if world still exists
                    return
                }
                if(!path.toFile().exists()) { // Cancel if path/file doesn't exist
                    cancel()
                    return
                }
                val deleted = path.toFile().deleteRecursively()
                if(deleted) {
                    logger.info("Game world '${world.name}' has been deleted")
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }

    @EventHandler
    private fun onWorldLoad(e: WorldLoadEvent) {
        if(e.world.generator == SGChunkGenerator) {
            logger.info("Game world '${e.world.name}' loaded, applying game rules")
            e.world.apply {
                isAutoSave = false
                isVoidDamageEnabled = false
                setGameRule(GameRules<Boolean>.ADVANCE_TIME, false)
                setGameRule(GameRules<Boolean>.ADVANCE_WEATHER, false)
                setGameRule(GameRules<Boolean>.RAIDS, false)
                setGameRule(GameRules<Boolean>.MOB_DROPS, false)
                setGameRule(GameRules<Boolean>.MOB_GRIEFING, false)
                setGameRule(GameRules<Boolean>.SPAWN_MOBS, false)
                setGameRule(GameRules<Boolean>.ALLOW_ENTERING_NETHER_USING_PORTALS, false)
                setGameRule(GameRules<Int>.RANDOM_TICK_SPEED, 0)
                setGameRule(GameRules<Boolean>.REDUCED_DEBUG_INFO, true)
                setGameRule(GameRules<Int>.RESPAWN_RADIUS, 0)
                setGameRule(GameRules<Int>.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0)
                setGameRule(GameRules<Boolean>.SHOW_ADVANCEMENT_MESSAGES, false)
                setGameRule(GameRules<Boolean>.SPAWNER_BLOCKS_WORK, false)
                setGameRule(GameRules<Boolean>.SPREAD_VINES, false)
                setGameRule(GameRules<Boolean>.LOCATOR_BAR, false)
                time = 10000
            }
        }
    }

    private fun getWorldCreator(worldId: UUID): WorldCreator {
        return WorldCreator("sg-game-$worldId", NamespacedKey(plugin, "sg-game-$worldId")).apply {
            generator(SGChunkGenerator)
            environment(World.Environment.NORMAL)
            type(WorldType.FLAT)
            generatorSettings("{\"layers\":[{\"block\":\"air\",\"height\":1}],\"biome\":\"plains\"}")
        }
    }
}