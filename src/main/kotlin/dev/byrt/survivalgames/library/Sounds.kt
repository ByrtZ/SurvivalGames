package dev.byrt.survivalgames.library

import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound

object Sounds {
    object Music {
        val LOBBY = sound(key("music.lobby"), Source.VOICE, 1f, 1f)
        val IN_GAME_INTRO = sound(key("music.game_intro"), Source.VOICE, 1f, 1f)
        val IN_GAME = sound(key("music.game_main"), Source.VOICE, 1f, 1f)
        val PRE_GAME_AUBURN_FOREST = sound(key("music.pre_game.auburn_forest"), Source.VOICE, 1f, 1f)
        val PRE_GAME_ROUGHWORKS = sound(key("music.pre_game.roughworks"), Source.VOICE, 1f, 1f)
        val PRE_GAME_MISTWOODS = sound(key("music.pre_game.mistwoods"), Source.VOICE, 1f, 1f)
        val PRE_GAME_HIGHLANDS = sound(key("music.pre_game.highlands"), Source.VOICE, 1f, 1f)
        val PRE_GAME_AELUMIA_CITADEL = sound(key("music.pre_game.aelumia_citadel"), Source.VOICE, 1f, 1f)
        val DOWNTIME_LOOP = sound(key("event.downtime.loop"), Source.VOICE, 1f, 1f)
        val DOWNTIME_SUSPENSE = sound(key("event.downtime.suspense"), Source.VOICE, 1f, 1f)
    }
    object Timer {
        val STARTING_TICK = sound(key("block.note_block.pling"), Source.MASTER, 1f, 1f)
        val STARTING_GO = sound(key("block.end_portal.spawn"), Source.MASTER, 1f, 1f)
        val CLOCK_TICK = sound(key("block.note_block.snare"), Source.MASTER, 1f, 1f)
        val CLOCK_TICK_HIGH = sound(key("block.note_block.snare"), Source.MASTER, 1f, 1.25f)
    }
    object Round {
        val ROUND_END = sound(key("block.respawn_anchor.deplete"), Source.MASTER, 1f, 1f)
        val GAME_OVER = sound(key("ui.toast.challenge_complete"), Source.MASTER, 0.75f, 1f)
        val OVERTIME_START = sound(key("block.portal.travel"), Source.MASTER, 0.15f, 0.75f)
    }
    object Score {
        val ELIMINATION = sound(key("entity.player.level_up"), Source.MASTER, 1f, 1f)
        val WIN_GAME = sound(key("entity.player.level_up"), Source.MASTER, 1f, 0.5f)
        val DEATH = sound(key("item.trident.thunder"), Source.MASTER, 5f, 1.2f)
        val DEATH_STATS = sound(key("item.trident.riptide_3"), Source.MASTER, 1f, 0f)
        val RESPAWN = sound(key("block.bubble_column.upwards_inside"), Source.MASTER, 2f, 0f)
    }
    object Alert {
        val ALARM = sound(key("block.trial_spawner.ominous_activate"), Source.MASTER, 3f, 0.75f)
        val HEALTH_DECREASE = sound(key("block.trial_spawner.ambient_ominous"), Source.MASTER, 1f, 1.25f)
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