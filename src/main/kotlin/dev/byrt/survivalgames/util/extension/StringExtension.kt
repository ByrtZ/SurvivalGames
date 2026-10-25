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

private val VOWELS = listOf('a', 'e', 'i', 'o', 'u')

/**
 * Check if the string begins with a vowel.
 * Case-insensitive, if empty or not a vowel returns false.
 * [VOWELS] are a, e, i, o, u.
 */
fun String.startsWithVowel(): Boolean {
    try {
        val firstChar = this.lowercase().first()
        return VOWELS.contains(firstChar)
    } catch (ex: NoSuchElementException) {
        return false
    }
}