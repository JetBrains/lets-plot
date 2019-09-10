package jetbrains.livemap.ui

import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
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
            .addComponent(ParentLayerComponent(myComponentManager.getEntity(UiLayerComponent::class).id))
            .addComponent(UiRenderComponent(renderBox))
    }

    fun addButton(renderBox: RenderBox): EcsEntity {
        return addParentLayerComponent(
            myComponentManager.createEntity("ui_button")
                .addComponent(ClickableComponent(renderBox))
                .addComponent(MouseInputComponent())
                .addComponent(EventListenerComponent()),
            renderBox
        )
    }
}