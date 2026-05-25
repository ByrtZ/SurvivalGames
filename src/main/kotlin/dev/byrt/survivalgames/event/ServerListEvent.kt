package dev.byrt.survivalgames.event

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Suppress("unused")
class ServerListEvent: Listener {
    @EventHandler
    private fun onServerPing(e: PaperServerListPingEvent) {
        e.version = "Byrtrium v${Bukkit.getMinecraftVersion()}"
        e.motd(Formatting.allTags.deserialize("${Translation.TabList.SERVER_LIST_PADDING}${Translation.TabList.SERVER_LIST_TITLE}${Translation.TabList.SERVER_LIST_PADDING}<newline>${Translation.TabList.SERVER_LIST_VERSION}${Translation.TabList.SERVER_LIST_GAME}${Translation.TabList.SERVER_LIST_EXTRA}"))
    }
}