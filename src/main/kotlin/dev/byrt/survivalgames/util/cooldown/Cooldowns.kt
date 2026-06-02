package dev.byrt.survivalgames.util.cooldown

import org.bukkit.entity.Player
import java.util.HashMap
import java.util.UUID

object Cooldowns {
    private var dataPointToolInteractions = HashMap<UUID, Long>()
    private const val DATA_POINT_TOOL_COOLDOWN_TIME = 2500
    fun attemptCreateDataPoint(player: Player): Boolean {
        return if(!dataPointToolInteractions.containsKey(player.uniqueId) || System.currentTimeMillis() - dataPointToolInteractions[player.uniqueId]!! > DATA_POINT_TOOL_COOLDOWN_TIME) {
            dataPointToolInteractions[player.uniqueId] = System.currentTimeMillis()
            true
        } else {
            false
        }
    }
}