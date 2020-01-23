/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.input.MouseInputComponent

class SearchingSystem(
    componentManager: EcsComponentManager,
    private val myIndexConsumer: (Int) -> Unit
): AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val mouseInput = cameraEntity.get<MouseInputComponent>()

        mouseInput.location?.let { location ->
            val entities = getEntities(COMPONENTS)

            val targets = entities.filter { entity ->
                entity.get<LocatorComponent>().locatorHelper.isCoordinateInTarget(explicitVec(location.x.toDouble(), location.y.toDouble()), entity)
            }.toList()

            if (targets.isNotEmpty()) {
                myIndexConsumer(targets.first().id)
            }
        }
    }

    companion object {
        val COMPONENTS = listOf(
            LocatorComponent::class
        )
    }
}