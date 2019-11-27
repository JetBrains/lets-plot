/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.tiles.components.CellStateComponent

class FragmentUpdateSystem(componentManager: EcsComponentManager, private val myEmptinessChecker: EmptinessChecker) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun initImpl(context: LiveMapContext) {
        createEntity("FragmentsChange")
            .addComponents {
                + ChangedFragmentsComponent()
                + EmptyFragmentsComponent()
            }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cellStateComponent = getSingleton<CellStateComponent>()
        val changedFragmentsComponent = getSingleton<ChangedFragmentsComponent>()
        val emptyFragments = getSingleton<EmptyFragmentsComponent>()

        val quadsToAdd = cellStateComponent.quadsToAdd
        val quadsToRemove = cellStateComponent.quadsToRemove

        val fragmentsToAdd = ArrayList<FragmentKey>()
        val fragmentsToRemove = ArrayList<FragmentKey>()

        // apply geocoded regionIds to RegionFragmentsComponents
        getEntities(REGION_COMPONENTS)
            .filter { it.get<RegionIdComponent>().regionId != null }
            .toList()
            .forEach {
                it.get<RegionFragmentsComponent>().id = it.get<RegionIdComponent>().regionId
                it.removeComponent(RegionIdComponent::class)
            }

        for (regionEntity in getEntities(RegionFragmentsComponent::class)) {
            regionEntity.get<RegionFragmentsComponent>().id?.let { regionId ->
                for (quad in quadsToAdd) {
                    if (!emptyFragments.contains(regionId, quad) && !myEmptinessChecker.test(regionId, quad)) {
                        fragmentsToAdd.add(FragmentKey(regionId, quad))
                    }
                }

                for (quad in quadsToRemove) {
                    if (!emptyFragments.contains(regionId, quad)) {
                        fragmentsToRemove.add(FragmentKey(regionId, quad))
                    }
                }
            }
        }

        changedFragmentsComponent.setToAdd(fragmentsToAdd)
        changedFragmentsComponent.setToRemove(fragmentsToRemove)
    }

    companion object {
        val REGION_COMPONENTS = listOf(
            RegionIdComponent::class,
            RegionFragmentsComponent::class
        )
    }
}