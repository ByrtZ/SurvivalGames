package dev.byrt.survivalgames.util.extension

import org.bukkit.Material

fun Material.name(): String =
    name.lowercase()
        .replace('_', ' ')
        .replaceFirstChar { it.uppercase() }