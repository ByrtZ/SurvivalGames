package dev.byrt.survivalgames.util.cooldown

import org.bukkit.entity.Player
import java.util.HashMap
import java.util.UUID

object Cooldowns {
    private var dataPointToolInteractions = HashMap<UUID, Long>()
    private const val DATA_POINT_TOOL_COOLDOWN_TIME = 1750
    fun attemptCreateDataPoint(player: Player): Boolean {
        return if(!dataPointToolInteractions.containsKey(player.uniqueId) || System.currentTimeMillis() - dataPointToolInteractions[player.uniqueId]!! > DATA_POINT_TOOL_COOLDOWN_TIME) {
            dataPointToolInteractions[player.uniqueId] = System.currentTimeMillis()
            true
        } else {
            false
        }
    }

    private var tntCooldowns = HashMap<UUID, Long>()
    private const val TNT_COOLDOWN_TIME = 500
    fun attemptUseTnt(player: Player): Boolean {
        return if(!tntCooldowns.containsKey(player.uniqueId) || System.currentTimeMillis() - tntCooldowns[player.uniqueId]!! > TNT_COOLDOWN_TIME) {
            tntCooldowns[player.uniqueId] = System.currentTimeMillis()
            true
        } else {
            false
        }
    }

    private var npcInteractionCooldowns = HashMap<UUID, Long>()
    private const val NPC_INTERACTION_COOLDOWN_TIME = 1250
    fun attemptNpcInteraction(player: Player): Boolean {
        return if(!npcInteractionCooldowns.containsKey(player.uniqueId) || System.currentTimeMillis() - npcInteractionCooldowns[player.uniqueId]!! > NPC_INTERACTION_COOLDOWN_TIME) {
            npcInteractionCooldowns[player.uniqueId] = System.currentTimeMillis()
            true
        } else {
            false
        }
    }

    private var spectatorCompassCooldowns = HashMap<UUID, Long>()
    private const val SPECTATOR_COMPASS_COOLDOWN_TIME = 1250
    fun attemptUseSpectatorCompass(player: Player): Boolean {
        return if(!spectatorCompassCooldowns.containsKey(player.uniqueId) || System.currentTimeMillis() - spectatorCompassCooldowns[player.uniqueId]!! > SPECTATOR_COMPASS_COOLDOWN_TIME) {
            spectatorCompassCooldowns[player.uniqueId] = System.currentTimeMillis()
            true
        } else {
            false
        }
    }
}