package dev.byrt.survivalgames.resource.registry

import dev.byrt.survivalgames.resource.RemotePack

/**
 * A remote resource pack registry.
 */
interface ResourcePackRegistry {

    /**
     * Fetches the latest pack for a given tag, or null if not known.
     * See [RemotePack.tag] for a description of tags
     */
    suspend fun fetchLatestForRef(tag: String): RemotePack?
}