package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.logger

class GameInstanceRounds(val instance: GameInstance) {
    private var round = 1
    private var totalRounds = 1

    fun nextRound() {
        if(round <= totalRounds) return
        round++
    }

    fun setRound(newRound : Int) {
        if (newRound == round) return
        logger.info("Round Updated: $round -> $newRound.")
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