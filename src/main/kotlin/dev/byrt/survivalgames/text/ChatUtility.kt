package dev.byrt.survivalgames.text

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.Formatting.allTags
import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtility {
    /** Sends a message to the admin channel which includes all online admins. **/
    fun broadcastAdmin(rawMessage: String, isSilent: Boolean) {
        val admin = Audience.audience(Bukkit.getOnlinePlayers()).filterAudience { (it as Player).hasPermission("sg.group.admin") }
        admin.sendMessage(allTags.deserialize("<speccolour>[<reset><prefix:admin><speccolour>]<reset> $rawMessage"))
        if(!isSilent) {
            admin.playSound(Sounds.Misc.ADMIN_MESSAGE)
        }
    }

    /** Sends a message to the dev channel which includes all online devs. **/
    fun broadcastDev(rawMessage: String, isSilent: Boolean) {
        val dev = Audience.audience(Bukkit.getOnlinePlayers()).filterAudience { (it as Player).hasPermission("sg.group.dev") }
        dev.sendMessage(allTags.deserialize("<speccolour>[<reset><prefix:dev><speccolour>]<reset> $rawMessage"))
        if(!isSilent) {
            dev.playSound(Sounds.Misc.ADMIN_MESSAGE)
        }
    }
}

object GlobalRenderer: ChatRenderer {
    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)
        return Component.text()
            .append(allTags.deserialize("<b>${source.sgPlayer().rank.rankHexTag}${source.sgPlayer().rank.rankBadge}"))
            .appendSpace()
            .append(Component.`object`(ObjectContents.playerHead(source.uniqueId)))
            .appendSpace()
            .append(source.displayName().color(if(source.isOp) NamedTextColor.DARK_RED else TextColor.fromHexString(source.sgPlayer().rank.rankHexTag)))
            .append(allTags.deserialize(":"))
            .appendSpace()
            .append(allTags.deserialize(plainMessage))
            .build()
    }
}