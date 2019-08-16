package jetbrains.livemap.entities.placement

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.placement.Components.WorldDimensionComponent.Companion.getDimension
import kotlin.math.pow

class WorldDimension2ScreenUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (camera().isIntegerZoom) {
            for (worldEntity in getEntities(COMPONENT_TYPES)) {
                val screenDimension = world2Screen(getDimension(worldEntity), camera().zoom)
                Components.ScreenDimensionComponent.provide(worldEntity).dimension = screenDimension
                ParentLayerComponent.tagDirtyParentLayer(worldEntity)
            }
        }
    }

    companion object {

        private val COMPONENT_TYPES = listOf(
            ZoomChangedComponent::class,
            Components.WorldDimensionComponent::class,
            ParentLayerComponent::class
        )

        fun world2Screen(p: DoubleVector, zoom: Double): DoubleVector {
            return p.mul(2.0.pow(zoom))
        }
    }
}