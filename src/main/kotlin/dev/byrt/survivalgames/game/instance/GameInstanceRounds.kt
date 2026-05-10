package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.text.ChatUtility

class GameInstanceRounds(val instance: GameInstance) {
    private var round = 1
    private var totalRounds = 1

    fun nextRound() {
        if(round <= totalRounds) return
        round++
    }

    fun setRound(newRound : Int) {
        if (newRound == round) return
        ChatUtility.broadcastDev(
            "<dark_gray>Round Updated: <red>$round<reset> <aqua>-> <green>$newRound<dark_gray>.",
            true
        )
        this.round = newRound
    }

    fun getRound() : Int {
        return round
    }

    fun setTotalRounds(newRounds: Int) {
        this.totalRounds = newRounds
    }

    fun getTotalRounds() : Int {
        return totalRounds
    }
}