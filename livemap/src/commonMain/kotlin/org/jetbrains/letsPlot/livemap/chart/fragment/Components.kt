/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.livemap.containers.LruCache
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity

class CachedFragmentsComponent : EcsComponent {
    private val myCache = HashMap<FragmentKey, EcsEntity>()

    internal operator fun contains(fragmentKey: FragmentKey) = myCache.containsKey(fragmentKey)

    fun keys() = myCache.keys

    fun store(fragmentKey: FragmentKey, fragmentEntity: EcsEntity) {
        if (myCache.containsKey(fragmentKey)) {
            error("Already existing fragment: ${fragmentEntity.name}")
        }

        myCache[fragmentKey] = fragmentEntity
    }

    operator fun get(fragmentKey: FragmentKey): EcsEntity? {
        return myCache[fragmentKey]
    }

    fun dispose(fragment: FragmentKey) {
        get(fragment)?.remove()
        myCache.remove(fragment)
    }
}

class EmptyFragmentsComponent : EcsComponent {

    private val myCache: LruCache<String, LruCache<QuadKey<LonLat>, Boolean>> = LruCache(REGIONS_CACHE_LIMIT)

    fun createCache(): LruCache<QuadKey<LonLat>, Boolean> {
        return LruCache(EMPTY_FRAGMENTS_CACHE_LIMIT)
    }

    internal fun add(fragmentKey: FragmentKey) {
        myCache.getOrPut(fragmentKey.regionId, ::createCache).put(fragmentKey.quadKey, true)
    }

    internal fun contains(regionId: String, quadKey: QuadKey<LonLat>): Boolean {
        val emptyQuads = myCache[regionId]
        return emptyQuads != null && emptyQuads.containsKey(quadKey)
    }

    fun addAll(emptyFragments: Set<FragmentKey>) {
        for (emptyFragment in emptyFragments) {
            add(emptyFragment)
        }
    }

    companion object {
        private const val EMPTY_FRAGMENTS_CACHE_LIMIT = 50000 // they are pretty cheap but have large impact on performance
        private const val REGIONS_CACHE_LIMIT = 5000 // safety net for dynamic regions (if ever)
    }
}

class ExistingRegionsComponent : EcsComponent {
    val existingRegions = HashSet<String>()
}

class ChangedFragmentsComponent : EcsComponent {
    private val myNewFragments = HashSet<FragmentKey>()
    private val myObsoleteFragments = HashSet<FragmentKey>()

    val requested: Set<FragmentKey>
        get() = myNewFragments

    val obsolete: Set<FragmentKey>
        get() = myObsoleteFragments

    fun setToAdd(keys: Collection<FragmentKey>) {
        myNewFragments.clear()
        myNewFragments.addAll(keys)
    }

    fun setToRemove(keys: Collection<FragmentKey>) {
        myObsoleteFragments.clear()
        myObsoleteFragments.addAll(keys)
    }

    fun anyChanges(): Boolean {
        return !myNewFragments.isEmpty() && myObsoleteFragments.isEmpty()
    }
}

class DownloadingFragmentsComponent : EcsComponent {
    val queue = HashMap<Int, MutableSet<FragmentKey>>()
    val downloading = HashSet<FragmentKey>()
    var downloaded: MutableMap<FragmentKey, MultiPolygon<Untyped>> = HashMap()
        set(fragments) {
            downloaded.clear()
            downloaded.putAll(fragments)
        }

    fun getZoomQueue(zoom: Int): MutableSet<FragmentKey> {
        return queue.getOrElse(zoom, ::HashSet)
    }

    fun extendQueue(keys: Set<FragmentKey>) {
        for (key in keys) {
            queue.getOrPut(key.zoom(), ::HashSet).add(key)
        }
    }

    fun reduceQueue(keys: Set<FragmentKey>) {
        for (key in keys) {
            queue[key.zoom()]?.remove(key)
        }
    }

    internal fun extendDownloading(keys: Set<FragmentKey>) {
        downloading.addAll(keys)
    }

    internal fun reduceDownloading(keys: Set<FragmentKey>) {
        downloading.removeAll(keys)
    }
}

class FragmentComponent(val fragmentKey: FragmentKey) : EcsComponent

class RegionBBoxComponent(val bbox: GeoRectangle) : EcsComponent

class RegionFragmentsComponent : EcsComponent {
    private val myFragmentEntities = HashSet<EcsEntity>()

    var fragments: Collection<EcsEntity>
        get() = myFragmentEntities
        set(fragments) {
            myFragmentEntities.clear()
            myFragmentEntities.addAll(fragments)
        }
}

class EmittedFragmentsComponent : EcsComponent {
    private val myEmitted = HashSet<FragmentKey>()

    fun setEmitted(fragments: Set<FragmentKey>): EmittedFragmentsComponent {
        myEmitted.clear()
        myEmitted.addAll(fragments)
        return this
    }

    internal fun keys(): Set<FragmentKey> {
        return myEmitted
    }
}

class EmittedRegionsComponent : EcsComponent {
    private val myEmitted = HashSet<String>()

    fun keys(): MutableSet<String> {
        return myEmitted
    }
}

/**
 * Fragment is counted as streaming when it is downloading or projecting.
 */
class StreamingFragmentsComponent : EcsComponent {
    private val fetching = HashMap<FragmentKey, EcsEntity?>()

    fun keys(): Set<FragmentKey> {
        return fetching.keys
    }

    internal fun add(key: FragmentKey) {
        fetching[key] = null
    }

    internal fun addAll(keys: Set<FragmentKey>) {
        keys.forEach { key -> fetching[key] = null }
    }

    internal operator fun set(key: FragmentKey, entity: EcsEntity) {
        fetching[key] = entity
    }

    internal fun getEntity(fragmentKey: FragmentKey): EcsEntity? {
        return fetching[fragmentKey]
    }

    internal fun remove(key: FragmentKey) {
        fetching.remove(key)
    }
}
