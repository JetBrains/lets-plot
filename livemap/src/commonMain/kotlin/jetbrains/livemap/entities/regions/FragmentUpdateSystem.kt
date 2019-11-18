/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.components.CellStateComponent

class FragmentUpdateSystem(componentManager: EcsComponentManager, private val myEmptinessChecker: EmptinessChecker) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun initImpl(context: LiveMapContext) {
        createEntity("FragmentsChange")
            .addComponent(ChangedFragmentsComponent())
            .addComponent(EmptyFragmentsComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cellStateComponent = getSingleton<CellStateComponent>()
        val changedFragmentsComponent = getSingleton<ChangedFragmentsComponent>()
        val emptyFragments = getSingleton<EmptyFragmentsComponent>()

        val quadsToAdd = cellStateComponent.quadsToAdd
        val quadsToRemove = cellStateComponent.quadsToRemove

        val fragmentsToAdd = ArrayList<FragmentKey>()
        val fragmentsToRemove = ArrayList<FragmentKey>()

        for (regionEntity in getEntities(RegionComponent::class)) {
            val regionId = regionEntity.get<RegionComponent>().id!!

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
        changedFragmentsComponent.setToAdd(fragmentsToAdd)
        changedFragmentsComponent.setToRemove(fragmentsToRemove)
    }
}