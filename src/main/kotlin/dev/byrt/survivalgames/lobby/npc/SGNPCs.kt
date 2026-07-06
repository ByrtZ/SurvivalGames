package dev.byrt.survivalgames.lobby.npc

import com.destroystokyo.paper.profile.ProfileProperty
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.util.Keys
import io.papermc.paper.datacomponent.item.ResolvableProfile
import io.papermc.paper.datacomponent.item.ResolvableProfile.SkinPatch
import org.bukkit.Bukkit
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.profile.PlayerTextures
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

@Suppress("unstableApiUsage")
object SGNPCs {
    private const val FALLBACK_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FlMWNiMzA5NzBlMDAwYmE1NjI4Mjc3MTQ3OGNkZDIyOTI4YjQxYWVjNTZkNTMzMzc0OWYxYjIwMTIxMDljZjUifX19"
    private val npcs = mutableListOf<Mannequin>()
    fun playNPCDialogue(player: Player, npcSoundSet: SGNPCSoundSet) {
        val numberTalkSounds = Random.nextInt(1, 5)
        object : BukkitRunnable() {
            var i = 1
            override fun run() {
                if(i <= numberTalkSounds) {
                    object : BukkitRunnable() {
                        override fun run() {
                            player.playSound(npcSoundSet.baseSound)
                            if(listOf(0, 0, 0, 1).random() == 0) {
                                player.playSound(npcSoundSet.hitSound)
                            }
                        }
                    }.runTaskLater(plugin, i * Random.nextInt(2, 5).toLong())
                } else {
                    this.cancel()
                }
                i++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun spawnAllNPCs() {
        for(npc in SGNPC.entries) {
            spawnNPC(npc)
        }
    }

    fun spawnNPC(npc: SGNPC) {
        val mannequin = npc.npcLocation.world.spawn(npc.npcLocation, Mannequin::class.java).apply {
            customName(Formatting.allTags.deserialize("<!i>${npc.npcNameColour}${npc.npcName}"))
            description = Formatting.allTags.deserialize(npc.npcDescription)
            isCustomNameVisible = true
            isInvulnerable = true
            isImmovable = true
            setGravity(false)
            pose = npc.npcPose
            persistentDataContainer.set(Keys.LOBBY_NPC, PersistentDataType.STRING, npc.toString())
            profile = ResolvableProfile.resolvableProfile().apply {
                addProperty(ProfileProperty("textures", npc.npcSkinTexture.ifEmpty { FALLBACK_TEXTURE }))
                skinPatch(SkinPatch.skinPatch().model(PlayerTextures.SkinModel.SLIM).build())
            }.build()
        }
        npcs.add(mannequin)
    }

    fun clearNPCs() {
        npcs.forEach { npc -> npc.remove() }
        npcs.clear()
        for(world in Bukkit.getWorlds()) {
            for(mannequin in world.getEntitiesByClass(Mannequin::class.java)) {
                if(mannequin.persistentDataContainer.has(Keys.LOBBY_NPC)) {
                    mannequin.remove()
                }
            }
        }
    }
}