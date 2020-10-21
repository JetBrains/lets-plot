/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.CanvasLayer
import jetbrains.livemap.core.rendering.layers.CanvasLayerComponent
import jetbrains.livemap.core.rendering.layers.LayersOrderComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent

class MouseInputDetectionSystem(componentManager: EcsComponentManager) : AbstractSystem<EcsContext>(componentManager) {

    private val myInteractiveEntityView: InteractiveEntityView = InteractiveEntityView()

    override fun updateImpl(context: EcsContext, dt: Double) {
        val entitiesByEventTypeAndZIndex = HashMap<MouseEventType, HashMap<Int, ArrayList<EcsEntity>>>()
        val canvasLayers = getSingleton<LayersOrderComponent>().canvasLayers

        getEntities(COMPONENTS).forEach { entity ->
            myInteractiveEntityView.setEntity(entity)

            MouseEventType.values().forEach { type ->
                if (myInteractiveEntityView.needToAdd(type)) {
                    myInteractiveEntityView
                        .addTo(
                            entitiesByEventTypeAndZIndex.getOrPut(type, ::HashMap),
                            getZIndex(entity, canvasLayers)
                        )
                }
            }
        }

        for (type in MouseEventType.values()) {
            val entitiesByZIndex = entitiesByEventTypeAndZIndex[type] ?: continue

            for (i in canvasLayers.size downTo 0) {
                entitiesByZIndex[i]?.let { acceptListeners(type, it) }
            }
        }
    }

    private fun acceptListeners(eventType: MouseEventType, entities: ArrayList<EcsEntity>) {
        entities.forEach { entity ->
            val input = entity.getComponent<MouseInputComponent>()
            val listeners = entity.getComponent<EventListenerComponent>()

            input.getEvent(eventType)?.let {
                if (!it.isStopped) {
                    for (listener in listeners.getListeners(eventType)) {
                        listener(it)
                    }
                }
            }
        }
    }

    private fun getZIndex(entity: EcsEntity, canvasLayers: List<CanvasLayer>): Int {
        return if (entity.contains<CameraComponent>()) 0 // if UI
        else {
            val canvasLayer = entity.componentManager
                .getEntityById(entity.get<ParentLayerComponent>().layerId)
                .get<CanvasLayerComponent>()
                .canvasLayer

            canvasLayers.indexOf(canvasLayer) + 1
        }
    }

    private class InteractiveEntityView {

        private lateinit var myInput: MouseInputComponent
        private lateinit var myClickable: ClickableComponent
        private lateinit var myListeners: EventListenerComponent
        private lateinit var myEntity: EcsEntity

        internal fun setEntity(entity: EcsEntity) {
            myEntity = entity
            myInput = entity.get()
            myClickable = entity.get()
            myListeners = entity.get()
        }

        fun needToAdd(type: MouseEventType): Boolean {
            return myInput
                .getEvent(type)
                .let {
                    it != null
                        && myListeners.contains(type)
                        && myClickable.rect.contains(it.location.toDoubleVector())
                }
        }

        fun addTo(map: HashMap<Int, ArrayList<EcsEntity>>, zIndex: Int) {
            map.getOrPut(zIndex, { ArrayList() }).add(myEntity)
        }
    }

    companion object {
        private val COMPONENTS = listOf(
            MouseInputComponent::class,
            ClickableComponent::class,
            EventListenerComponent::class
        )
    }
}