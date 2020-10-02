/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.Vector
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.ui.CursorService

class CursorSystem(componentManager: EcsComponentManager, private val myCursorService: CursorService) : AbstractSystem<EcsContext>(componentManager) {

    private var isPointer: Boolean = false
    private val myInput = MouseInputComponent()

    override fun initImpl(context: EcsContext) {
        componentManager.createEntity("CursorInputComponent").add(myInput)
    }

    override fun updateImpl(context: EcsContext, dt: Double) {
        myInput.location?.let { location ->
            if (!isPointer && isHover(location)) {
                myCursorService.pointer()
                isPointer = true
            } else if (isPointer && !isHover(location)) {
                myCursorService.default()
                isPointer = false
            }
        }
    }

    private fun isHover(location: Vector): Boolean {
        return getEntities(CursorPointerComponent::class).any { entity ->
            entity.get<CursorPointerComponent>().rect.contains(location.toDoubleVector())
        }
    }
}