package dev.byrt.survivalgames.game.mechanic.bounty

import org.bukkit.entity.Player
import java.util.UUID

data class SGBounty(val bountyID: UUID, val bountyName: String, val bountyHunter: Player, val bountyTarget: Player)
