/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.intersects
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.geocoding.RegionIdComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridStateComponent

class FragmentUpdateSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun initImpl(context: LiveMapContext) {
        createEntity("FragmentsChange")
            .addComponents {
                + ChangedFragmentsComponent()
                + EmptyFragmentsComponent()
                + ExistingRegionsComponent()
            }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewportGridState = getSingleton<ViewportGridStateComponent>()
        val changedFragmentsComponent = getSingleton<ChangedFragmentsComponent>()
        val emptyFragments = getSingleton<EmptyFragmentsComponent>()
        val existingRegions = getSingleton<ExistingRegionsComponent>().existingRegions

        val quadsToRemove = viewportGridState.quadsToRemove

        val fragmentsToAdd = ArrayList<FragmentKey>()
        val fragmentsToRemove = ArrayList<FragmentKey>()

        for (regionEntity in getEntities(REGION_ENTITY_COMPONENTS)) {
            val bbox = regionEntity.get<RegionBBoxComponent>().bbox
            val regionId = regionEntity.get<RegionIdComponent>().regionId

            var quadsToAdd = viewportGridState.quadsToLoad

            if (!existingRegions.contains(regionId)) {
                quadsToAdd = viewportGridState.visibleQuads
                existingRegions.add(regionId)
            }

            for (quad in quadsToAdd) {
                if (!emptyFragments.contains(regionId, quad) && bbox.intersect(quad)) {
                    fragmentsToAdd.add(FragmentKey(regionId, quad))
                }
            }

            for (quad in quadsToRemove) {
                if (!emptyFragments.contains(regionId, quad)) {
                    fragmentsToRemove.add(FragmentKey(regionId, quad))
                }
            }
        }

        changedFragmentsComponent.setToAdd(fragmentsToAdd)
        changedFragmentsComponent.setToRemove(fragmentsToRemove)
    }

    private fun GeoRectangle.intersect(quadKey: QuadKey<LonLat>): Boolean {
        val quadKeyRect = quadKey.computeRect()

        splitByAntiMeridian().forEach { bbox ->
            if (bbox.intersects(quadKeyRect)) {
                return true
            }
        }

        return false
    }

    companion object {
        val REGION_ENTITY_COMPONENTS = listOf(
            RegionIdComponent::class,
            RegionBBoxComponent::class,
            RegionFragmentsComponent::class
        )
    }
}