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
        val STARTING_TICK = sound(key("block.note_block.hat"), Source.RECORD, 1f, 1f)
        val STARTING_123 = sound(key("block.note_block.harp"), Source.RECORD, 1f, 1f)
        val STARTING_GO = sound(key("block.end_portal.spawn"), Source.PLAYER, 1f, 1f)
        val CLOCK_TICK = sound(key("block.note_block.hat"), Source.RECORD, 1f, 1f)
        val CLOCK_TICK_HIGH = sound(key("block.note_block.hat"), Source.RECORD, 1f, 1.25f)
        val CLOCK_TICK_BASS = sound(key("block.note_block.bass"), Source.RECORD, 1f, 1f)
        val CLOCK_TICK_BASS_HIGH = sound(key("block.note_block.bass"), Source.RECORD, 1f, 1.5f)
    }
    object Round {
        val ROUND_END = sound(key("block.respawn_anchor.deplete"), Source.PLAYER, 1f, 1f)
        val GAME_OVER = sound(key("ui.toast.challenge_complete"), Source.PLAYER, 0.75f, 1f)
        val OVERTIME_START = sound(key("block.portal.travel"), Source.PLAYER, 0.15f, 0.75f)
    }
    object Score {
        val ELIMINATION = sound(key("entity.player.levelup"), Source.PLAYER, 0.5f, 1f)
        val WIN_GAME = sound(key("entity.player.levelup"), Source.PLAYER, 1f, 0.5f)
        val WIN_GAME_HORN = sound(key("item.goat_horn.sound.1"), Source.PLAYER, 3f, 1.5f)
        val DEATH = sound(key("item.trident.thunder"), Source.PLAYER, 5f, 1.2f)
        val DEATH_BACKGROUND = sound(key("item.trident.return"), Source.PLAYER, 2f, 0f)
        val DEATH_STATS = sound(key("item.trident.riptide_3"), Source.PLAYER, 1f, 0f)
        val RESPAWN = sound(key("block.bubble_column.upwards_inside"), Source.PLAYER, 2f, 0f)
    }
    object Alert {
        val ALARM = sound(key("block.trial_spawner.ominous_activate"), Source.PLAYER, 3f, 0.75f)
        val HEALTH_DECREASE = sound(key("block.trial_spawner.ambient_ominous"), Source.PLAYER, 1f, 1.25f)
        val GRACE_PERIOD_BEGIN = sound(key("entity.ender_eye.death"), Source.PLAYER, 1f, 1f)
        val GRACE_PERIOD_END = sound(key("block.respawn_anchor.deplete"), Source.PLAYER, 0.75f, 0.75f)
        val SUPPLY_DROP_SPAWN = sound(key("block.note_block.bit"), Source.RECORD, 0.75f, 1.25f)
        val GAME_AUTO_START_INITIATED = sound(key("entity.wither.death"), Source.PLAYER, 0.5f, 1.25f)
        val GAME_AUTO_START_CANCELLED = sound(key("entity.wither.spawn"), Source.PLAYER, 0.5f, 1.5f)
    }
    object Tutorial {
        const val TUTORIAL_POP = "entity.item.pickup"
    }
    object Misc {
        val ADMIN_MESSAGE = sound(key("ui.button.click"), Source.PLAYER, 0.25f, 2f)
        val INTERFACE_INTERACT = sound(key("block.vault.eject_item"), Source.PLAYER, 1f, 1f)
        val INTERFACE_ENTER_SUB_MENU = sound(key("block.vault.activate"), Source.PLAYER, 1f, 1f)
        val INTERFACE_BACK = sound(key("block.vault.deactivate"), Source.PLAYER, 1f, 1f)
        val INTERFACE_ERROR = sound(key("block.vault.reject_rewarded_player"), Source.PLAYER, 1f, 1f)
        val TITLE_SCREEN_ENTER = sound(key("entity.breeze.shoot"), Source.PLAYER, 1f, 0.75f)
        val NPC_INTERACT = sound(key("entity.villager.trade"), Source.PLAYER, 1f, 1f)
        val NPC_INTERACT_HIT = sound(key("entity.villager.hurt"), Source.PLAYER, 1f, 1.25f)
        val LEVEL_UP = sound(key("entity.illusioner.prepare_mirror"), Source.PLAYER, 5f, 1f)
        val RANK_UP = sound(key("entity.lightning_bolt.thunder"), Source.PLAYER, 5f, 1f)
    }
}