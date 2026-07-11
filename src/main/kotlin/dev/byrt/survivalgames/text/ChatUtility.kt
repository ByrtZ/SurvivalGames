package dev.byrt.survivalgames.text

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.Formatting.allTags
import dev.byrt.survivalgames.text.Formatting.restrictedTags
import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtility {
    /** Sends a message to the admin channel which includes all online staff. **/
    fun broadcastStaff(rawMessage: String, isSilent: Boolean) {
        val staff = Audience.audience(Bukkit.getOnlinePlayers()).filterAudience { (it as Player).hasPermission("sg.staff_chat") }
        staff.sendMessage(allTags.deserialize("<speccolour>[<reset><prefix:staff><speccolour>]<reset> $rawMessage"))
        if(!isSilent) {
            staff.playSound(Sounds.Misc.ADMIN_MESSAGE)
        }
    }

    /** Sends a message to the admin channel which includes all online admins. **/
    fun broadcastAdmin(rawMessage: String, isSilent: Boolean) {
        val admin = Audience.audience(Bukkit.getOnlinePlayers()).filterAudience { (it as Player).hasPermission("sg.admin_chat") }
        admin.sendMessage(allTags.deserialize("<speccolour>[<reset><prefix:admin><speccolour>]<reset> $rawMessage"))
        if(!isSilent) {
            admin.playSound(Sounds.Misc.ADMIN_MESSAGE)
        }
    }

    /** Sends a message to the dev channel which includes all online devs. **/
    fun broadcastDev(rawMessage: String, isSilent: Boolean) {
        val dev = Audience.audience(Bukkit.getOnlinePlayers()).filterAudience { (it as Player).hasPermission("sg.dev_chat") }
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
            .append(source.displayName().color(if(source.hasPermission("sg.group.admin") || source.hasPermission("sg.group.dev")) NamedTextColor.DARK_RED else if(source.hasPermission("sg.group.staff")) TextColor.fromHexString("#FFE600") else TextColor.fromHexString(source.sgPlayer().rank.rankHexColour)))
            .append(allTags.deserialize(":"))
            .appendSpace()
            .append(if(source.isOp) allTags.deserialize(plainMessage) else restrictedTags.deserialize(plainMessage))
            .build()
    }
}