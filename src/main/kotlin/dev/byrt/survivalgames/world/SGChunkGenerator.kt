package dev.byrt.survivalgames.world

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.Random

object SGChunkGenerator: ChunkGenerator() {
    override fun canSpawn(world: World, x: Int, z: Int): Boolean = false
    override fun shouldGenerateCaves(): Boolean = false
    override fun shouldGenerateNoise(): Boolean = false
    override fun shouldGenerateMobs(): Boolean = false
    override fun shouldGenerateSurface(): Boolean = false
    override fun shouldGenerateDecorations(): Boolean = false
    override fun shouldGenerateStructures(): Boolean = false
    override fun getFixedSpawnLocation(world: World, random: Random): Location {
        return Location(world, 0.0, 0.0, 0.0, 0f, 0f)
    }
}