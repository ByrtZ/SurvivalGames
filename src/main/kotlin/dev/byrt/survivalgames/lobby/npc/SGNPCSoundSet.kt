package dev.byrt.survivalgames.lobby.npc

import dev.byrt.survivalgames.library.Sounds
import net.kyori.adventure.sound.Sound

enum class SGNPCSoundSet(val baseSound: Sound, val hitSound: Sound) {
    GENERIC_NPC_DIALOGUE(Sounds.Misc.NPC_INTERACT, Sounds.Misc.NPC_INTERACT_HIT)
}