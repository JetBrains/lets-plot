/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.input.ClickableComponent
import jetbrains.livemap.core.input.EventListenerComponent
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox
import jetbrains.livemap.ui.UiRenderingTaskSystem.UiLayerComponent

class UiService(private val myComponentManager: EcsComponentManager, val resourceManager: ResourceManager) {

    fun addRenderable(renderBox: RenderBox): EcsEntity {
        return addParentLayerComponent(myComponentManager.createEntity("ui_renderable"), renderBox)
    }

    private fun addParentLayerComponent(entity: EcsEntity, renderBox: RenderBox): EcsEntity {
        return entity
            .addComponents {
                + ParentLayerComponent(myComponentManager.getEntity(UiLayerComponent::class).id)
                + UiRenderComponent(renderBox)
            }
    }

    fun addButton(renderBox: RenderBox): EcsEntity {
        return addParentLayerComponent(
            myComponentManager
                .createEntity("ui_button")
                .addComponents {
                    + ClickableComponent(renderBox)
                    + MouseInputComponent()
                    + EventListenerComponent()
                },
            renderBox
        )
    }
}
