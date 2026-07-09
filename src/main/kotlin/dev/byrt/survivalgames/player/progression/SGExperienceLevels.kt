package dev.byrt.survivalgames.player.progression

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.data.Rank
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration

object SGExperienceLevels {
    fun setLevel(player: Player, newLevel: SGLevel) {
        val sgPlayer = player.sgPlayer()
        sgPlayer.level = newLevel
        sgPlayer.exp = 0
        sgPlayer.rank = newLevel.rankAssociated
    }

    fun appendExperience(player: Player, expToAppend: Int) {
        val sgPlayer = player.sgPlayer()
        val currentLevel = sgPlayer.level
        val currentExperience = sgPlayer.exp
        val currentRank = sgPlayer.rank

        player.sendActionBar(Formatting.allTags.deserialize("$SG_FONT_TAG+$expToAppend <playercolour>XP"))
        if(currentLevel != SGLevel.LEVEL_26) {
            if(currentExperience + expToAppend >= currentLevel.requiredXp) {
                if(currentExperience + expToAppend > currentLevel.requiredXp) {
                    val overflowXp = (currentExperience + expToAppend) - currentLevel.requiredXp
                    sgPlayer.exp = overflowXp
                } else {
                    sgPlayer.exp = 0
                }
                // Level up
                val nextLevel = SGLevel.entries[currentLevel.ordinal + 1]
                val newRank: Rank? = if(currentRank != nextLevel.rankAssociated) nextLevel.rankAssociated else null

                val prefix = Formatting.allTags.deserialize("$SG_FONT_TAG<b>${nextLevel.rankAssociated.rankHexTag}LEVEL UP!<reset>")
                val newLevelText = Formatting.allTags.deserialize("${SG_FONT_TAG}You are now <b>${nextLevel.rankAssociated.rankHexTag}${nextLevel.levelName}</b>.")

                player.showTitle(Title.title(prefix, newLevelText, Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(500))))
                player.sendMessage(Component.text().append(prefix).appendSpace().append(newLevelText).build())
                player.playSound(Sounds.Misc.LEVEL_UP)
                if(newRank != null) {
                    sgPlayer.rank = newRank
                    for(p in Bukkit.getOnlinePlayers()) {
                        p.playSound(Sounds.Misc.RANK_UP)
                        p.sendMessage(Formatting.allTags.deserialize("<newline>${SG_FONT_TAG}${newRank.rankHexTag}<b>${player.name}</b> <white>ranked up to <b>${newRank.rankHexTag}${newRank.rankName.uppercase()}</b><white>!</font><newline>"))
                    }
                }
                sgPlayer.level = nextLevel
            } else {
                sgPlayer.exp = currentExperience + expToAppend
            }
        } else {
            sgPlayer.rank = currentRank
            sgPlayer.exp = 0
            sgPlayer.level = currentLevel
        }
    }
}