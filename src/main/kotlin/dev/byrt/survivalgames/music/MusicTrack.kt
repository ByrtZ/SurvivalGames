package dev.byrt.survivalgames.music

import dev.byrt.survivalgames.library.Sounds
import net.kyori.adventure.sound.Sound

enum class MusicTrack(val sound: Sound, val lengthSecs: Int) {
    LOBBY(Sounds.Music.LOBBY, 131),
    IN_GAME(Sounds.Music.IN_GAME, 338),
    PRE_GAME_AUBURN_FOREST(Sounds.Music.PRE_GAME_AUBURN_FOREST, 300),
    PRE_GAME_ROUGHWORKS(Sounds.Music.PRE_GAME_ROUGHWORKS, 295),
    PRE_GAME_MISTWOODS(Sounds.Music.PRE_GAME_MISTWOODS, 183),
    PRE_GAME_HIGHLANDS(Sounds.Music.PRE_GAME_HIGHLANDS, 306),
    PRE_GAME_AELUMIA_CITADEL(Sounds.Music.PRE_GAME_AELUMIA_CITADEL, 250);
}