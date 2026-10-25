package dev.byrt.survivalgames.lobby.npc

import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.interfaces.SGMatchmakingInterface
import dev.byrt.survivalgames.item.SGItem
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.loot.SGLoot
import dev.byrt.survivalgames.loot.items.SGItems
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.scheduler.BukkitRunnable

enum class SGNPC(val npcName: String, val npcDescription: String, val npcNameColour: String, val npcLocation: Location, val npcSkinTexture: String, var npcPose: Pose = Pose.STANDING, val onInteract: (Player, SGNPC, Mannequin) -> Unit = { _, _, _ -> }) {
    LOBBY_MATCHMAKING(
        "${SG_FONT_TAG}<b>Survival Games",
        "${SG_FONT_TAG}<b><yellow>CLICK TO INTERACT",
        "<playercolour>",
        Location(Bukkit.getWorlds()[0], -1914.5, 71.0, -1654.5, -180f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc2OTQ1NTczMzU1MywKICAicHJvZmlsZUlkIiA6ICJiM2E1M2VlMDgwMmI0NzE5OGVkM2VhMjRmYjZmNmQ3YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOZXRhbnlhaHVQdlAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE4MTVjMDhjMmU0OTUxMDE0ZWM4YzA2ZTJiYmI5ODM4M2UwM2NjZGU3Mzg0M2RlY2Y4MzE4YzNjMmFhNWVmOSIKICAgIH0KICB9Cn0=",
        onInteract = { player, _, _ ->
            defaultCoroutineScope.launch { SGMatchmakingInterface.create(player) }
        }
    ),
    LOBBY_FISHING_ROD(
    "${SG_FONT_TAG}Rodney Gibbs",
    "${SG_FONT_TAG}<i>Get yer fishin' rods!",
    "<playercolour>",
    Location(Bukkit.getWorlds()[0], -1887.5, 71.0, -1630.5, 135f, 0f),
    "ewogICJ0aW1lc3RhbXAiIDogMTc2Nzk3NzM1ODk3NywKICAicHJvZmlsZUlkIiA6ICIyYjcyZWYyYWUzMmQ0Zjc1OGEyMThlMDI4MTViYmNjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ2b2xrb2RhZl82MyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYzkzOGUwYmFjOGM1MjJkZDBiNmIwZWE1MDQ2YjQ3NDlmZjM0YWZiMzdhZTFiYzc0NTIzZGMxOThhYjllZmJkIgogICAgfQogIH0KfQ==",
    onInteract = { player, npc, _ ->
            if(player.inventory.contains(Material.FISHING_ROD)) {
                player.sendMessage(Formatting.allTags.deserialize("${SG_FONT_TAG}<b>${npc.npcNameColour}${npc.npcName}</b><white>: Don't be greedy, matey. Ye already possess a rod!"))
                SGNPCs.playNPCDialogue(player, SGNPCSoundSet.GENERIC_NPC_DIALOGUE)
            } else {
                player.sendMessage(Formatting.allTags.deserialize("${SG_FONT_TAG}<b>${npc.npcNameColour}${npc.npcName}</b><white>: Ready to catch some fishies, me hearty?"))
                SGNPCs.playNPCDialogue(player, SGNPCSoundSet.GENERIC_NPC_DIALOGUE)
                object : BukkitRunnable() {
                    override fun run() {
                        val fishingRod = SGLoot.getItem(SGItems.FISHING_ROD, 1)
                        player.sendMessage(Formatting.allTags.deserialize("${SG_FONT_TAG}<playercolour>You received</playercolour>: ").append(fishingRod.effectiveName().hoverEvent(fishingRod)))
                        player.give(fishingRod)
                        player.playSound(Sounds.Alert.GRACE_PERIOD_BEGIN)
                    }
                }.runTaskLater(plugin, 10L)
            }
        }
    )
}