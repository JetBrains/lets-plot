/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext

class RegionEmitSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    private val myRegionIndex: Utils.RegionsIndex = Utils.RegionsIndex(componentManager)
    private val myPendingFragments = HashMap<String, PendingFragments>()
    private var myPendingZoom = -1

    override fun initImpl(context: LiveMapContext) {
        createEntity("emitted_regions").add(EmittedRegionsComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (context.camera.isZoomFractionChanged && context.camera.isZoomLevelChanged) {
            myPendingZoom = context.camera.zoom.toInt()
            myPendingFragments.clear()
        }

        getSingleton<ChangedFragmentsComponent>().requested.forEach(::wait)
        getSingleton<ChangedFragmentsComponent>().obsolete.forEach(::remove)
        getSingleton<EmittedFragmentsComponent>().keys().forEach(::accept)

        val emittedRegionsComponent = getSingleton<EmittedRegionsComponent>()
        emittedRegionsComponent.keys().clear()

        for (readyRegion in checkReadyRegions()) {
            emittedRegionsComponent.keys().add(readyRegion)
            renderRegion(readyRegion)
        }
    }

    private fun renderRegion(regionId: String) {
        val region = myRegionIndex.find(regionId)

        val fragmentsCache = getSingleton<CachedFragmentsComponent>()

        region.get<RegionFragmentsComponent>().run {
            fragments = myPendingFragments[regionId]!!
                .readyFragments()
                .mapNotNull(fragmentsCache::get)
        }

        ParentLayerComponent.tagDirtyParentLayer(region)
    }

    private fun wait(fragmentKey: FragmentKey) {
        if (myPendingZoom != fragmentKey.zoom()) {
            return
        }

        myPendingFragments.getOrPut(fragmentKey.regionId, RegionEmitSystem::PendingFragments).waitFragment(fragmentKey)
    }

    private fun accept(fragmentKey: FragmentKey) {
        if (myPendingZoom != fragmentKey.zoom()) {
            return
        }

        myPendingFragments[fragmentKey.regionId]?.accept(fragmentKey)
    }

    private fun remove(fragmentKey: FragmentKey) {
        if (myPendingZoom != fragmentKey.zoom()) {
            return
        }

        myPendingFragments[fragmentKey.regionId]?.remove(fragmentKey)
    }

    private fun checkReadyRegions(): Collection<String> {
        val readyRegions = ArrayList<String>()

        myPendingFragments.forEach { (regionId, pendingFragments) ->
            if (pendingFragments.checkDone()) {
                readyRegions.add(regionId)
            }
        }

        return readyRegions
    }

    internal class PendingFragments {
        private val myWaitingFragments = HashSet<FragmentKey>()
        private val myReadyFragments = HashSet<FragmentKey>()
        private var myIsDone = false

        fun waitFragment(fragmentKey: FragmentKey) {
            myWaitingFragments.add(fragmentKey)
            myIsDone = false
        }

        fun accept(fragmentKey: FragmentKey) {
            myReadyFragments.add(fragmentKey)
            remove(fragmentKey)
        }

        fun remove(fragmentKey: FragmentKey) {
            myWaitingFragments.remove(fragmentKey)
            if (myWaitingFragments.isEmpty()) {
                myIsDone = true
            }
        }

        fun checkDone(): Boolean {
            if (myIsDone) {
                myIsDone = false
                return true
            }

            return false
        }

        fun readyFragments(): Collection<FragmentKey> {
            return myReadyFragments
        }
    }
}