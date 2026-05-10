package dev.byrt.survivalgames.world

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.util.*

object SGWorldCreator: Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    fun createNewGameWorld(worldId: UUID): World {
        val job = copyRegionFiles(worldId)
        var newWorld: World? = null
        object : BukkitRunnable() {
            var timeElapsed = 0
            override fun run() {
                if(job.isCompleted) {
                    logger.info("Region files job completed (in ${timeElapsed.times(50)}ms), creating game world ($worldId)")
                    newWorld = getWorldCreator(worldId).createWorld()!!
                    logger.info("Created game world ($worldId)")
                    cancel()
                }
                timeElapsed++
            }
        }.runTaskTimer(plugin, 0L, 1)
        return newWorld!!
    }

    @EventHandler
    private fun onWorldLoad(e: WorldLoadEvent) {
        if(e.world.generator == SGChunkGenerator) {
            logger.info("Game world (${e.world.name}) loaded, applying game rules")
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
            }
        }
    }

    private fun getWorldCreator(worldId: UUID): WorldCreator {
        return WorldCreator("sg-game-$worldId", NamespacedKey(plugin, "world")).apply {
            generator(SGChunkGenerator)
            environment(World.Environment.CUSTOM)
            type(WorldType.FLAT)
            generatorSettings("{\"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\":\"the_void\"")
        }
    }

    private fun copyRegionFiles(worldId: UUID): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            File("${plugin.dataFolder}/worlds/master")
                .copyRecursively(
                    target = File("${plugin.dataFolder}/worlds/temp/sg-game-$worldId"),
                    overwrite = true,
                )
        }
    }
}