package dev.byrt.survivalgames.library

import dev.byrt.survivalgames.plugin
import org.bukkit.Bukkit

object Translation {
    object Generic {
        const val ARROW_PREFIX = "[<yellow>▶<reset>] "
        const val ITEM_RECEIVED_PREFIX = "<green>(\uD83D\uDCB0) "
        const val TITLE_SCREEN_ACTIONBAR = "<white>Press <burbcolour><key:key.sneak></burbcolour> <white>to join the fight."
        const val DEATH_PREFIX = "<gray>[<#ff3333><unicodeprefix:skull><gray>]<reset> "
    }
    object Tutorial {
        const val BLANK_LINE = "<newline>"
        const val STANDBY = "<red><italic>Standby for the game to begin...<reset>"
    }
    object Teams {
        const val JOIN_TEAM = "You are now on team %d%s<reset>."
        const val LEAVE_TEAM = "You are no longer on team %d%s<reset.>"
        const val JOIN_SPECTATOR = "You are now a <speccolour>Spectator<reset>."
        const val LEAVE_SPECTATOR = "You are no longer a <speccolour>Spectator<reset>."
    }
    object Overtime {
        const val OVERTIME_PREFIX = "<#ff3333><bold>OVERTIME: "
        const val OVERTIME_REASON = "survivalgames.game.state.overtime.prefix"
    }
    object TabList {
        const val SERVER_LIST_PADDING = "<gold>■<light_aqua>■<gold>■<light_aqua>■<gold>■<light_aqua>■<gold>■<light_aqua>■<gold>■<light_aqua>■<gold>■<light_aqua>■<gold>■<light_aqua>■<gold>■"
        const val SERVER_LIST_TITLE = " <light_purple><bold>???<reset> "
        val SERVER_LIST_VERSION = "<dark_gray>${Bukkit.getMinecraftVersion()}<reset>"
        const val SERVER_LIST_GAME = "<white> ● <yellow>The zombies are coming.<reset>"
        val SERVER_LIST_EXTRA = "<white> ● <dark_gray>${plugin.pluginMeta.displayName}<reset>"
    }
}