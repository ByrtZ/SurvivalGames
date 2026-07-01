package dev.byrt.survivalgames.player.progression

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.data.Rank
import dev.byrt.survivalgames.player.data.SGPlayerData
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.time.Duration

object SGExperienceLevels {
    fun getLevel(player: Player): SGLevel = SGLevel.valueOf(SGPlayerData.getPlayerConfiguration(player).get("${player.uniqueId}${SGPlayerData.LEVEL_PATH}").toString())

    fun setLevel(player: Player, newLevel: SGLevel) {
        val config = SGPlayerData.getPlayerConfiguration(player)
        config.set("${player.uniqueId}${SGPlayerData.LEVEL_PATH}", newLevel.name)
        config.set("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}", 0)
        config.set("${player.uniqueId}${SGPlayerData.RANK_PATH}", newLevel.rankAssociated.toString())
        config.save(File("${plugin.dataFolder}/players", "${player.uniqueId}.yml"))

        val level = SGLevel.valueOf(config.get("${player.uniqueId}${SGPlayerData.LEVEL_PATH}").toString()).ordinal + 1
        val exp = config.get("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}") as Int / SGLevel.valueOf(config.get("${player.uniqueId}${SGPlayerData.LEVEL_PATH}").toString()).requiredXp.toFloat()
        val rank = Rank.valueOf(config.get("${player.uniqueId}${SGPlayerData.RANK_PATH}").toString())
        player.level = level
        player.exp = if(exp > 1f) 1f else exp
        player.sgPlayer().rank = rank
    }

    fun appendExperience(player: Player, expToAppend: Int) {
        val experienceGained = expToAppend
        player.sendActionBar(Formatting.allTags.deserialize("$SG_FONT_TAG+${experienceGained} <playercolour>XP"))
        val config = SGPlayerData.getPlayerConfiguration(player)
        val currentExperience = config.get("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}") as Int
        val currentLevel = SGLevel.valueOf(config.get("${player.uniqueId}${SGPlayerData.LEVEL_PATH}").toString())
        val currentRank = player.sgPlayer().rank

        if(currentLevel != SGLevel.LEVEL_26) {
            if(currentExperience + experienceGained >= currentLevel.requiredXp) {
                if(currentExperience + experienceGained > currentLevel.requiredXp) {
                    val overflowXp = (currentExperience + experienceGained) - currentLevel.requiredXp
                    config.set("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}", overflowXp)
                } else {
                    config.set("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}", 0)
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
                    config.set("${player.uniqueId}${SGPlayerData.RANK_PATH}", newRank.toString())
                    for(p in Bukkit.getOnlinePlayers()) {
                        p.playSound(Sounds.Misc.RANK_UP)
                        p.sendMessage(Formatting.allTags.deserialize("${SG_FONT_TAG}${newRank.rankHexTag}<b>${player.name}</b> <white>ranked up to <b>${newRank.rankHexTag}${newRank.rankName.uppercase()}</b><white>!"))
                    }
                }
                config.set("${player.uniqueId}${SGPlayerData.LEVEL_PATH}", nextLevel.name)
            } else {
                config.set("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}", currentExperience + experienceGained)
            }
            config.save(File("${plugin.dataFolder}/players","${player.uniqueId}.yml"))

            // Set visuals for level and experience
            val level = SGLevel.valueOf(config.get("${player.uniqueId}${SGPlayerData.LEVEL_PATH}").toString()).ordinal + 1
            val exp = config.get("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}") as Int / SGLevel.valueOf(config.get("${player.uniqueId}${SGPlayerData.LEVEL_PATH}").toString()).requiredXp.toFloat()
            val rank = Rank.valueOf(config.get("${player.uniqueId}${SGPlayerData.RANK_PATH}").toString())
            player.level = level
            player.exp = if(exp > 1f) 1f else exp
            player.sgPlayer().rank = rank
        } else {
            config.set("${player.uniqueId}${SGPlayerData.LEVEL_PATH}", SGLevel.LEVEL_26.name)
            config.set("${player.uniqueId}${SGPlayerData.EXPERIENCE_PATH}", 0)
            config.set("${player.uniqueId}${SGPlayerData.RANK_PATH}", Rank.ROYAL.toString())
            config.save(File("${plugin.dataFolder}/players","${player.uniqueId}.yml"))
            player.level = 26
            player.exp = 0f
            player.sgPlayer().rank = Rank.ROYAL
        }
    }
}