/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.spatial.computeRect
import jetbrains.datalore.base.typedGeometry.intersects
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.entities.geocoding.RegionBBoxComponent
import jetbrains.livemap.entities.geocoding.RegionIdComponent
import jetbrains.livemap.tiles.components.CellStateComponent

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
        val cellStateComponent = getSingleton<CellStateComponent>()
        val changedFragmentsComponent = getSingleton<ChangedFragmentsComponent>()
        val emptyFragments = getSingleton<EmptyFragmentsComponent>()
        val existingRegions = getSingleton<ExistingRegionsComponent>().existingRegions

        val quadsToRemove = cellStateComponent.quadsToRemove

        val fragmentsToAdd = ArrayList<FragmentKey>()
        val fragmentsToRemove = ArrayList<FragmentKey>()

        for (regionEntity in getEntities(REGION_ENTITY_COMPONENTS)) {
            val bbox = regionEntity.get<RegionBBoxComponent>().bbox
            val regionId = regionEntity.get<RegionIdComponent>().regionId

            var quadsToAdd = cellStateComponent.quadsToAdd

            if (!existingRegions.contains(regionId)) {
                quadsToAdd = cellStateComponent.visibleQuads
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