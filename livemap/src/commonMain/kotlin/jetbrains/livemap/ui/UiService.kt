/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.ecs.onEachEntity
import jetbrains.livemap.core.input.*
import jetbrains.livemap.core.rendering.controls.Button
import jetbrains.livemap.core.rendering.layers.DirtyCanvasLayerComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox
import jetbrains.livemap.ui.UiRenderingTaskSystem.UiLayerComponent

class UiService(
    private val myComponentManager: EcsComponentManager,
    val resourceManager: ResourceManager
) {

    fun repaint() {
        val uiLayerEntityId = myComponentManager.getEntity(UiLayerComponent::class).id
        myComponentManager.getEntityById(uiLayerEntityId).tag(::DirtyCanvasLayerComponent)
    }

    fun addRenderable(renderBox: RenderBox, name: String = "ui_renderable"): EcsEntity {
        return addParentLayerComponent(myComponentManager.createEntity(name), renderBox)
    }

    private fun addParentLayerComponent(entity: EcsEntity, renderBox: RenderBox): EcsEntity {
        return entity
            .addComponents {
                + ParentLayerComponent(myComponentManager.getEntity(UiLayerComponent::class).id)
                + UiRenderComponent(renderBox)
            }
    }

    fun addButton(button: Button): EcsEntity {
        button.attach(this)
        return addParentLayerComponent(
            myComponentManager
                .createEntity(button.name)
                .addComponents {
                    + CursorStyleComponent(CursorStyle.POINTER)
                    + ClickableComponent(button)
                    + MouseInputComponent()
                    + EventListenerComponent().apply {
                        addClickListener(button::dispatchClick)
                        addDoubleClickListener(button::dispatchDoubleClick)
                    }
                },
            button
        )
    }

    fun addButton(renderBox: RenderBox): EcsEntity {
        return addParentLayerComponent(
            myComponentManager
                .createEntity("ui_button")
                .addComponents {
                    + CursorStyleComponent(CursorStyle.POINTER)
                    + ClickableComponent(renderBox)
                    + MouseInputComponent()
                    + EventListenerComponent()
                },
            renderBox
        )
    }

    fun addLink(renderBox: RenderBox): EcsEntity {
        return myComponentManager
            .createEntity("ui_link")
            .addComponents {
                + ParentLayerComponent(myComponentManager.getEntity(UiLayerComponent::class).id)
                + CursorStyleComponent(CursorStyle.POINTER)
                + ClickableComponent(renderBox)
                + MouseInputComponent()
                + EventListenerComponent()
            }
    }

    private fun findEntity(obj: RenderBox): EcsEntity? {
        myComponentManager.onEachEntity<UiRenderComponent> { entity, uiComponent ->
            if (uiComponent.renderBox === obj) {
                return entity
            }
        }

        return null
    }

    fun remove(obj: RenderBox) {
        findEntity(obj)?.let(myComponentManager::removeEntity)
    }

    fun setCursor(obj: RenderBox, cursorStyle: CursorStyle) {
        findEntity(obj)?.setComponent(CursorStyleComponent(cursorStyle))
    }

    fun defaultCursor(obj: RenderBox) {
        findEntity(obj)?.remove<CursorStyleComponent>()
    }
}
