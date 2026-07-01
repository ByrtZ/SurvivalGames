package dev.byrt.survivalgames.nametag.provider

import dev.byrt.survivalgames.nametag.NameTagProvider
import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.text.Formatting

class LobbyNameTagProvider : NameTagProvider() {
    override val lines = 2
    override fun update(player: SGPlayer) {
        val tag = player.nameTag ?: return
        tag[0] = Formatting.allTags.deserialize(if(player.bukkitPlayer().isOp) "<prefix:admin>" else "${player.rank.rankHexTag}<b>${player.rank.rankPlate}</b>")
        tag[1] = Formatting.allTags.deserialize(if(player.bukkitPlayer().isOp) "<dark_red>${player.playerName}" else "${player.rank.rankHexTag}${player.playerName}")
    }
}