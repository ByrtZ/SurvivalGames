package dev.byrt.survivalgames.util.extension

import org.bukkit.Material
import java.util.UUID

fun Material.name(): String =
    name.lowercase()
        .replace('_', ' ')
        .replaceFirstChar { it.uppercase() }

fun String.clean(): String =
    this.lowercase()
        .replace('_', ' ')
        .replaceFirstChar { it.uppercase() }

fun UUID.trimmed(): String =
    this.toString().toCharArray(0, 8).joinToString("")