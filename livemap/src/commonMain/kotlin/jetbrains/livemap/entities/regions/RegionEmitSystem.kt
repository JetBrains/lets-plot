/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.regions.Utils.zoom

class RegionEmitSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    private val myRegionIndex: Utils.RegionsIndex = Utils.RegionsIndex(componentManager)
    private val myPendingFragments = HashMap<String, PendingFragments>()
    private var myPendingZoom = -1

    override fun initImpl(context: LiveMapContext) {
        createEntity("emitted_regions").addComponent(EmittedRegionsComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        camera().ifZoomChanged {
            if (camera().isIntegerZoom) {
                myPendingZoom = camera().zoom.toInt()
                myPendingFragments.clear()
            }
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

        region.get<RegionComponent>().run {
            fragments = myPendingFragments[regionId]!!
                .readyFragments()
                .mapNotNull(fragmentsCache::get)
        }

        ParentLayerComponent.tagDirtyParentLayer(region)
    }

    private fun wait(fragmentKey: FragmentKey) {
        if (myPendingZoom != zoom(fragmentKey)) {
            return
        }

        myPendingFragments.getOrPut(fragmentKey.regionId, ::PendingFragments).waitFragment(fragmentKey)
    }

    private fun accept(fragmentKey: FragmentKey) {
        if (myPendingZoom != zoom(fragmentKey)) {
            return
        }

        myPendingFragments[fragmentKey.regionId]?.accept(fragmentKey)
    }

    private fun remove(fragmentKey: FragmentKey) {
        if (myPendingZoom != zoom(fragmentKey)) {
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