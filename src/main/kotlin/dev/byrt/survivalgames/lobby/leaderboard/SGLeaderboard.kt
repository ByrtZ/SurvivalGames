package dev.byrt.survivalgames.lobby.leaderboard

import org.bukkit.Bukkit
import org.bukkit.Location

enum class SGLeaderboard(val leaderboardName: String, val refreshInMins: Int = 15, val reversedSorting: Boolean = false, val location: Location) {
    WINS(leaderboardName = "Wins", location = Location(Bukkit.getWorlds()[0], -1945.375, 72.125, -1655.5, -90f, 0f)),
    ELIMINATIONS(leaderboardName = "Eliminations", location = Location(Bukkit.getWorlds()[0], -1940.5, 72.125, -1645.625, 180f, 0f)),
    MATCHES_PLAYED(leaderboardName = "Matches Played", location = Location(Bukkit.getWorlds()[0], -1933.5, 72.125, -1645.625, 180f, 0f)),
}