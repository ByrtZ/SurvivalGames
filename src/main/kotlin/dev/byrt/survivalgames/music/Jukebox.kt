package dev.byrt.survivalgames.music

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.plugin
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

object Jukebox {
    private val jukeboxMap = mutableMapOf<UUID, BukkitRunnable>()
    fun startMusicLoop(player: Player, music: JukeboxTrack) {
        if (jukeboxMap.containsKey(player.uniqueId)) {
            for (tracks in JukeboxTrack.entries) {
                stopMusicLoop(player, tracks)
            }
        }
        when (music) {
            JukeboxTrack.IN_GAME -> {
                player.playSound(Sounds.Music.IN_GAME_INTRO)
                runMusicTask(player, music, 26L * 20L)
            }
            else -> runMusicTask(player, music)
        }
    }

    private fun runMusicTask(player: Player, music: JukeboxTrack, delay: Long = 0L) {
        val bukkitRunnable = object : BukkitRunnable() {
            var musicTimer = 0
            override fun run() {
                if (musicTimer == 0) {
                    player.playSound(music.sound, Sound.Emitter.self())
                }
                if (musicTimer == music.lengthSecs) {
                    musicTimer = -1
                }
                musicTimer++
            }
        }
        bukkitRunnable.runTaskTimer(plugin, delay, 20L)
        jukeboxMap[player.uniqueId] = bukkitRunnable
    }

    fun stopMusicLoop(player: Player, music: JukeboxTrack) {
        player.stopSound(music.sound)
        jukeboxMap.remove(player.uniqueId)?.cancel()
    }

    fun disconnect(player: Player) {
        for (music in JukeboxTrack.entries) {
            stopMusicLoop(player, music)
        }
    }
}