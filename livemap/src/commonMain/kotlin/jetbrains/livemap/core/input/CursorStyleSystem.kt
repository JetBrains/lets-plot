/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.ui.CursorService

class CursorStyleSystem(componentManager: EcsComponentManager, private val myCursorService: CursorService) : AbstractSystem<EcsContext>(componentManager) {
    private val myInput = MouseInputComponent()

    override fun initImpl(context: EcsContext) {
        componentManager.createEntity("CursorInputComponent").add(myInput)
    }

    override fun updateImpl(context: EcsContext, dt: Double) {
        myInput.moveEvent?.location?.let { location ->
            getEntities(COMPONENT_TYPES).find { entity ->
                location.inside(entity.get<ClickableComponent>())
            }?.let { entity ->
                when (entity.get<CursorStyleComponent>().cursorStyle) {
                    CursorStyle.POINTER -> myCursorService.pointer()
                }
            } ?: myCursorService.default()
        }
    }


    companion object {
        private val COMPONENT_TYPES = listOf(
            CursorStyleComponent::class,
            ClickableComponent::class
        )
    }
}