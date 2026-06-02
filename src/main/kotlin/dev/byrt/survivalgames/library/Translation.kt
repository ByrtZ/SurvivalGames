package dev.byrt.survivalgames.library

import dev.byrt.survivalgames.plugin
import org.bukkit.Bukkit

@Deprecated("To be moved to translations.")
object Translation {
    object Generic {
        const val ARROW_PREFIX = "[<yellow>▶<reset>] "
    }
    object TabList {
        const val SERVER_LIST_PADDING = "<gold>■<yellow>■<gold>■<yellow>■<gold>■<yellow>■<gold>■<yellow>■<gold>■<yellow>■<gold>■<yellow>■<gold>■<yellow>■<gold>■"
        const val SERVER_LIST_TITLE = " <gold><bold>MC.BYRT.DEV<reset> "
        val SERVER_LIST_VERSION = "<dark_gray>${Bukkit.getMinecraftVersion()}<reset>"
        const val SERVER_LIST_GAME = "<white> ● <yellow>Fine, I'll make SG myself.<reset>"
        val SERVER_LIST_EXTRA = "<white> ● <dark_gray>${plugin.pluginMeta.displayName}<reset>"
    }
}