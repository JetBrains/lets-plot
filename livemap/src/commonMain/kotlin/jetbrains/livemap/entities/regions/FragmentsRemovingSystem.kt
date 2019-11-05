/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.regions.Utils.zoom
import jetbrains.livemap.tiles.components.CellStateComponent

class FragmentsRemovingSystem(private val myCacheSize: Int, componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        if (!getSingletonComponent<ChangedFragmentsComponent>().anyChanges()) {
            return
        }

        val requestedFragments = getSingletonComponent<ChangedFragmentsComponent>().requested

        val keepStreaming = HashSet<FragmentKey>()
        run {
            val streamingFragments = getSingletonComponent<StreamingFragmentsComponent>()
            val dropStreaming = HashSet<FragmentKey>()
            if (requestedFragments.isNotEmpty()) {
                val requestedZoom = zoom(requestedFragments.first())
                for (fragmentKey in streamingFragments.keys()) {
                    if (zoom(fragmentKey) == requestedZoom) {
                        keepStreaming.add(fragmentKey) // not visible, but soon will be
                    } else {
                        dropStreaming.add(fragmentKey) // wrong zoom - remove fragments and stop microthreads
                    }
                }
            }

            // Force drop obsolete streaming fragments
            dropStreaming.forEach { fragmentKey ->
                streamingFragments.getEntity(fragmentKey)?.remove()
                streamingFragments.remove(fragmentKey)
            }
        }

        val activeFragments = HashSet<FragmentKey>()
        for (region in getEntities(RegionComponent::class)) {
            activeFragments.addAll(
                region.get<RegionComponent>().fragments.map {
                    it.get<FragmentComponent>().fragmentKey
                }
            )
        }

        val fragmentsCache = getSingletonComponent<CachedFragmentsComponent>()
        val visibleQuads = getSingletonComponent<CellStateComponent>().visibleQuads

        val fragmentsToRemove = HashSet(fragmentsCache.keys())
        fragmentsToRemove.addAll(getSingletonComponent<ChangedFragmentsComponent>().obsolete)
        fragmentsToRemove.removeAll(requestedFragments)
        fragmentsToRemove.removeAll(activeFragments) // currently used by region, including scaled fragments from previous zoom
        fragmentsToRemove.removeAll(keepStreaming) // not visible, but soon will be
        fragmentsToRemove.removeAll { fragment -> visibleQuads.contains(fragment.quadKey) } // may be not active - waiting other region fragments

        var toRemoveCount = fragmentsToRemove.size - myCacheSize
        val removeIt = fragmentsToRemove.iterator()
        while (removeIt.hasNext() && toRemoveCount-- > 0) {
            val fragmentToRemove = removeIt.next()
            if (fragmentsCache.contains(fragmentToRemove)) {
                fragmentsCache.dispose(fragmentToRemove)
            }
        }
    }
}