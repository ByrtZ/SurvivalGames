package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.Bukkit
import org.bukkit.GameMode

object Game {
    fun start() {
        if(GameManager.getGameState() == GameState.IDLE) {
            GameManager.nextState()
        } else {
            ChatUtility.broadcastDev("<red>Unable to start game, as game is already running.", false)
        }
    }

    fun stop() {
        if(GameManager.getGameState() == GameState.IDLE) {
            ChatUtility.broadcastDev("<red>Unable to stop game, as no game is running.", false)
        } else {
            GameManager.setGameState(GameState.GAME_END)
        }
    }

    fun reload() {
        GameInfo.updateStatus()
        GameInfo.updateRound()
        GameInfo.updateTimer()
        for(player in Bukkit.getOnlinePlayers()) {
            //SpawnPoints.respawnLocation(player)
            player.gameMode = GameMode.ADVENTURE
            GameManager.teams.setTeam(player, null)
        }
    }

    fun setup() {
        GameInfo.buildScoreboard()
    }

    fun cleanup() {
        GameInfo.destroyScoreboard()
    }
}