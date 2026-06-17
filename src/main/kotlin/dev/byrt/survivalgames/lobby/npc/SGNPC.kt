package dev.byrt.survivalgames.lobby.npc

import dev.byrt.survivalgames.interfaces.SGInterfaces
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.entity.Pose

enum class SGNPC(val npcName: String, val npcDescription: String, val npcNameColour: String, val npcLocation: Location, val npcSkinTexture: String, var npcPose: Pose = Pose.STANDING, val onInteract: (Player, SGNPC, Mannequin) -> Unit = { _, _, _ -> }) {
    LOBBY_MATCHMAKING(
        "${SG_FONT_TAG}<b>Survival Games",
        "${SG_FONT_TAG}<b><yellow>CLICK TO INTERACT",
        "<playercolour>",
        Location(Bukkit.getWorlds()[0], -1914.5, 71.0, -1654.5, -180f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc2OTQ1NTczMzU1MywKICAicHJvZmlsZUlkIiA6ICJiM2E1M2VlMDgwMmI0NzE5OGVkM2VhMjRmYjZmNmQ3YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOZXRhbnlhaHVQdlAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE4MTVjMDhjMmU0OTUxMDE0ZWM4YzA2ZTJiYmI5ODM4M2UwM2NjZGU3Mzg0M2RlY2Y4MzE4YzNjMmFhNWVmOSIKICAgIH0KICB9Cn0=",
        onInteract = { player, _, _ ->
            runBlocking { SGInterfaces.createMatchmakingInterface(player) }
        }
    )
}