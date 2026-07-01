package dev.byrt.survivalgames.player.data

enum class SGStatistics(val statistic: SGStatistic) {
    RANK(SGStatistic("Rank", ".rank")),
    LEVEL(SGStatistic("Level", ".level")),
    EXPERIENCE(SGStatistic("Experience", ".experience")),
    ELIMINATIONS(SGStatistic("Eliminations", ".statistic.eliminations")),
    WINS(SGStatistic("Wins", ".statistic.wins")),
    GAMES_PLAYED(SGStatistic("Games Played", ".statistic.games_played"));
}