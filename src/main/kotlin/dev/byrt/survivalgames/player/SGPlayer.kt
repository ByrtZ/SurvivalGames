package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

class SGPlayer(val uuid: UUID, val playerName: String, var playerType: PlayerType) {
    init {
        logger.info("Player Manager: Registered player ${this.playerName} as SGPlayer.")
    }

    fun setType(newType: PlayerType) {
        if(newType == this.playerType) return
        this.playerType = newType
        when(newType) {
            PlayerType.IDLE -> {
                this.bukkitPlayer().gameMode = GameMode.ADVENTURE
                this.bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(this.bukkitPlayer().isOp) "<prefix:admin> " else ""}${this.bukkitPlayer().name}"))
            }
            PlayerType.SPECTATOR -> {
                if (currentContainer == null) this.bukkitPlayer().gameMode = GameMode.ADVENTURE else this.bukkitPlayer().gameMode = GameMode.SPECTATOR
                this.bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(this.bukkitPlayer().isOp) "<prefix:admin> " else ""}<gray>${this.bukkitPlayer().name}"))
            }
            PlayerType.PARTICIPANT -> {
                this.bukkitPlayer().gameMode = GameMode.ADVENTURE
                this.bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(this.bukkitPlayer().isOp) "<prefix:admin> " else ""}<playercolour>${this.bukkitPlayer().name}"))
            }
            PlayerType.UNREGISTERED -> {
                this.bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(this.bukkitPlayer().isOp) "<prefix:admin> " else ""}<#0>${this.bukkitPlayer().name}"))
            }
        }
        logger.info("Type: ${this.playerName} now has value ${this.playerType}.")
    }

    var isDead: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            logger.info("Dead State: ${this.playerName} now has value $value")
        }

    var currentContainer: GameContainer? = null
        set(value) {
            if (field == value) return
            field = value
            logger.info("Container: ${this.playerName} now has value ${value?.containerId}")
        }

    fun bukkitPlayer(): Player {
        return Bukkit.getPlayer(this.uuid)!!
    }
}

enum class PlayerType {
    IDLE,
    SPECTATOR,
    PARTICIPANT,
    UNREGISTERED
}