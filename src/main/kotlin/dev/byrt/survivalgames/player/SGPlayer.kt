package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.nametag.NameTag
import dev.byrt.survivalgames.nametag.NameTagProvider
import dev.byrt.survivalgames.nametag.provider.GameNameTagProvider
import dev.byrt.survivalgames.nametag.provider.LobbyNameTagProvider
import dev.byrt.survivalgames.player.data.Rank
import dev.byrt.survivalgames.player.progression.SGLevel
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

class SGPlayer(val uuid: UUID, val playerName: String, var playerType: PlayerType) {
    init {
        logger.info("Player Manager: Registered player ${this.playerName} as SGPlayer.")
    }

    var exp = 0
        set(value) {
            if (field == value) return
            field = value
            PlayerVisuals.updateXp(this.bukkitPlayer())
        }
    var level = SGLevel.LEVEL_1
        set(value) {
            if (field == value) return
            field = value
            PlayerVisuals.updateLevel(this.bukkitPlayer())
        }

    var rank: Rank = Rank.RECRUIT
        set(value) {
            if (field == value) return
            field = value
            if(nameTagProvider != null) nameTagProvider?.update(this)
            setTabName()
        }

    var eliminations = 0
        set(value) {
            if (field == value) return
            field = value
        }
    var wins = 0
        set(value) {
            if (field == value) return
            field = value
        }
    var matchesPlayed = 0
        set(value) {
            if (field == value) return
            field = value
        }

    fun setType(newType: PlayerType) {
        if(newType == this.playerType) return
        this.playerType = newType
        nameTagProvider = null // Always remove existing name tag prior to teleportation, because you obviously cannot teleport a player with passengers... also delay setting name tag later for safety
        when(newType) {
            PlayerType.IDLE -> {
                Bukkit.getScheduler().runTaskLater(plugin, Runnable { nameTagProvider = LobbyNameTagProvider() }, 20L)
            }
            PlayerType.SPECTATOR -> {
                if (currentContainer == null) bukkitPlayer().gameMode = GameMode.ADVENTURE else bukkitPlayer().gameMode = GameMode.SPECTATOR
            }
            PlayerType.PARTICIPANT -> {
                bukkitPlayer().gameMode = GameMode.ADVENTURE
                Bukkit.getScheduler().runTaskLater(plugin, Runnable { nameTagProvider = GameNameTagProvider() }, 20L)
            } else -> {}
        }
        setTabName()
        logger.info("Type: ${this.playerName} now has value ${this.playerType}.")
    }

    private fun setTabName() {
        when(playerType) {
            PlayerType.IDLE -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().isOp) "<prefix:admin> <dark_red>" else "${rank.rankHexTag}<b>${rank.rankPlate}</b> "}${this.playerName}"))
            PlayerType.SPECTATOR -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().isOp) "<prefix:admin> " else "<b>${rank.rankHexTag}${rank.rankPlate}<reset> "}<gray>${this.playerName}"))
            PlayerType.PARTICIPANT -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().isOp) "<prefix:admin> " else "<b>${rank.rankHexTag}${rank.rankPlate}<reset> "}<playercolour>${this.playerName}"))
            PlayerType.UNREGISTERED -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().isOp) "<prefix:admin> " else "<b>${rank.rankHexTag}${rank.rankPlate}<reset> "}<#0>${this.playerName}"))
        }
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

    /* NameTag related properties and methods */
    var nameTag: NameTag? = null
        private set

    var nameTagProvider: NameTagProvider? = null
        set(value) {
            field?.destroy(this)
            field = value ?: run {
                removeNameTag()
                logger.info("Nametag: ${this.playerName} now has no nametag.")
                return
            }
            ensureNameTag(value.lines)
            value.initialise(this)
            value.update(this)
            logger.info("Nametag: ${this.playerName} now has value $value")
        }

    private fun removeNameTag() {
        nameTag?.close()
        nameTag = null
    }

    fun refreshNameTag() {
        nameTagProvider?.update(this)
    }

    private fun ensureNameTag(lines: Int) {
        if (nameTag?.lines == lines) return
        removeNameTag()
        nameTag = NameTag(bukkitPlayer(), lines)
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