package dev.byrt.survivalgames.library

import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound

object Sounds {
    object Music {
        val DOWNTIME_LOOP = sound(key("event.downtime.loop"), Source.VOICE, 1f, 1f)
        val DOWNTIME_SUSPENSE = sound(key("event.downtime.suspense"), Source.VOICE, 1f, 1f)
    }
    object Timer {
        val STARTING_123 = sound(key("block.note_block.harp"), Source.VOICE, 1f, 1f)
        val STARTING_GO = sound(key("block.note_block.harp"), Source.VOICE, 1.25f, 1f)
        val CLOCK_TICK = sound(key("block.note_block.snare"), Source.VOICE, 1f, 1f)
        val CLOCK_TICK_HIGH = sound(key("block.note_block.snare"), Source.VOICE, 1f, 1.25f)
    }
    object Round {
        val ROUND_END = sound(key("block.respawn_anchor.deplete"), Source.VOICE, 1f, 1f)
        val GAME_OVER = sound(key("ui.toast.challenge_complete"), Source.VOICE, 0.75f, 1f)
    }
    object Score {
        val ELIMINATION = sound(key(""), Source.VOICE, 1f, 1f)
        val DEATH = sound(key(""), Source.VOICE, 1f, 1f)
        val DEATH_STATS = sound(key(""), Source.VOICE, 0.75f, 1f)
        val RESPAWN = sound(key(""), Source.VOICE, 2f, 0.0f)
    }
    object Alert {
        val ALARM = sound(key(""), Source.VOICE, 1.25f, 1f)
    }
    object Tutorial {
        const val TUTORIAL_POP = "entity.item.pickup"
    }
    object Misc {
        val ADMIN_MESSAGE = sound(key("ui.button.click"), Source.MASTER, 0.5f, 2f)
        val INTERFACE_INTERACT = sound(key("block.vault.insert_item"), Source.MASTER, 1f, 1f)
        val INTERFACE_ENTER_SUB_MENU = sound(key("block.vault.activate"), Source.MASTER, 1f, 1f)
        val INTERFACE_BACK = sound(key("block.vault.deactivate"), Source.MASTER, 1f, 1f)
        val INTERFACE_ERROR = sound(key("block.vault.reject_rewarded_player"), Source.MASTER, 1f, 1f)
        val TITLE_SCREEN_ENTER = sound(key("entity.breeze.shoot"), Source.MASTER, 1f, 0.75f)
    }
}