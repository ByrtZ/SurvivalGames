package dev.byrt.survivalgames.nametag.provider

import dev.byrt.survivalgames.nametag.NameTagProvider
import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.entity.Display

class LobbyNameTagProvider : NameTagProvider() {
    override val lines = 2
    override fun update(player: SGPlayer) {
        val tag = player.nameTag ?: return
        tag[0] = Formatting.allTags.deserialize(if(player.bukkitPlayer().hasPermission("sg.group.admin") || player.bukkitPlayer().hasPermission("sg.group.dev")) "<prefix:admin>" else if(player.bukkitPlayer().hasPermission("sg.group.staff")) "<prefix:staff>" else "${player.rank.rankHexTag}<b>${player.rank.rankPlate}</b>")
        tag[0].brightness = Display.Brightness(15, 15)
        tag[1] = Formatting.allTags.deserialize(if(player.bukkitPlayer().hasPermission("sg.group.admin") || player.bukkitPlayer().hasPermission("sg.group.dev")) "<dark_red>${player.playerName}" else if(player.bukkitPlayer().hasPermission("sg.group.staff")) "<playercolour>${player.playerName}" else "${player.rank.rankHexTag}${player.playerName}")
        tag[1].brightness = Display.Brightness(15, 15)
    }
}