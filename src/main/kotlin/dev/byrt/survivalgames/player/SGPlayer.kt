package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.nametag.NameTag
import dev.byrt.survivalgames.nametag.NameTagProvider
import dev.byrt.survivalgames.nametag.provider.GameNameTagProvider
import dev.byrt.survivalgames.nametag.provider.LobbyNameTagProvider
import dev.byrt.survivalgames.player.data.Rank
import dev.byrt.survivalgames.player.progression.SGLevel
import dev.byrt.survivalgames.player.visuals.PlayerVisibility
import dev.byrt.survivalgames.player.visuals.PlayerVisuals
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
                bukkitPlayer().gameMode = GameMode.ADVENTURE
                isHidden = false
                canFly = false
                Bukkit.getScheduler().runTaskLater(plugin, Runnable { nameTagProvider = LobbyNameTagProvider() }, 20L)
            }
            PlayerType.SPECTATOR -> {
                bukkitPlayer().gameMode = GameMode.ADVENTURE
                isHidden = true
                canFly = true
            }
            PlayerType.PARTICIPANT -> {
                bukkitPlayer().gameMode = GameMode.ADVENTURE
                isHidden = false
                canFly = false
                Bukkit.getScheduler().runTaskLater(plugin, Runnable { nameTagProvider = GameNameTagProvider() }, 20L)
            } else -> {}
        }
        setTabName()
        logger.info("Type: ${this.playerName} now has value ${this.playerType}.")
    }

    private fun setTabName() {
        if(bukkitPlayer().hasPermission("sg.group.admin")) {
            bukkitPlayer().playerListOrder = 99
        } else if(bukkitPlayer().hasPermission("sg.group.dev")) {
            bukkitPlayer().playerListOrder = 98
        } else if(bukkitPlayer().hasPermission("sg.group.staff")) {
            bukkitPlayer().playerListOrder = 97
        } else {
            bukkitPlayer().playerListOrder = when(rank) {
                Rank.RECRUIT -> 1
                Rank.SOLDIER -> 2
                Rank.CONSTABLE -> 3
                Rank.WINGS -> 4
                Rank.GENDARME -> 5
                Rank.ROYAL -> 6
            }
        }
        when(playerType) {
            PlayerType.IDLE -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().hasPermission("sg.group.admin") || bukkitPlayer().hasPermission("sg.group.dev")) "<prefix:admin> <dark_red>" else if(bukkitPlayer().hasPermission("sg.group.staff")) "<prefix:staff> <playercolour>" else "${rank.rankHexTag}<b>${rank.rankPlate}</b> "}${this.playerName}"))
            PlayerType.SPECTATOR -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().hasPermission("sg.group.admin") || bukkitPlayer().hasPermission("sg.group.dev")) "<prefix:admin> " else if(bukkitPlayer().hasPermission("sg.group.staff")) "<prefix:staff> " else "<b>${rank.rankHexTag}${rank.rankPlate}<reset> "}<gray>${this.playerName}"))
            PlayerType.PARTICIPANT -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().hasPermission("sg.group.admin") || bukkitPlayer().hasPermission("sg.group.dev")) "<prefix:admin> " else if(bukkitPlayer().hasPermission("sg.group.staff")) "<prefix:staff> " else "<b>${rank.rankHexTag}${rank.rankPlate}<reset> "}<playercolour>${this.playerName}"))
            PlayerType.UNREGISTERED -> bukkitPlayer().playerListName(Formatting.allTags.deserialize("<!i>${if(bukkitPlayer().hasPermission("sg.group.admin") || bukkitPlayer().hasPermission("sg.group.dev")) "<prefix:admin> " else if(bukkitPlayer().hasPermission("sg.group.staff")) "<prefix:staff> " else "<b>${rank.rankHexTag}${rank.rankPlate}<reset> "}<#0>${this.playerName}"))
        }
    }

    var isDead: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            logger.info("Dead State: ${this.playerName} now has value $value")
        }

    var isHidden: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if(field) PlayerVisibility.hide(this)
            if(!field) PlayerVisibility.show(this)
            logger.info("Hidden: ${this.playerName} now has value $value")
        }

    var canFly: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if(field) this.bukkitPlayer().allowFlight = true
            if(!field) this.bukkitPlayer().allowFlight = false
            logger.info("Flight: ${this.playerName} now has value $value")
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