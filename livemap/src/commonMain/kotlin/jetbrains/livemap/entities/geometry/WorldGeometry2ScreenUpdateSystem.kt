package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.maps.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.datalore.maps.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.placement.Components
import jetbrains.livemap.entities.placement.Components.WorldOriginComponent
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.projections.ProjectionUtil


class WorldGeometry2ScreenUpdateSystem(private val myQuantIterations: Int, componentManager: EcsComponentManager) :
    LiveMapSystem(componentManager) {

    private fun createScalingTask(entity: EcsEntity, zoom: Int): MicroTask<Unit> {

        // Fix ghosting after zoom by removing outdated screen geometry.
        if (!entity.contains(ScaleComponent::class)) {
            entity.remove<ScreenGeometryComponent>()
        }

        val worldOrigin = entity.get<WorldOriginComponent>().origin.let { DoubleVector(it.x, it.y) }
        val zoomProjection = ProjectionUtil.square(ProjectionUtil.zoom(zoom))
        return GeometryTransform
            .simple(entity.get<WorldGeometryComponent>().geometry!!.asMultipolygon()) {
                zoomProjection.project(it.subtract(worldOrigin))
            }
            .map { screenMultipolygon ->
                runLaterBySystem(entity) { theEntity ->
                    ParentLayerComponent.tagDirtyParentLayer(theEntity)
                    theEntity.provide(::ScreenGeometryComponent).apply {
                        geometry = ClientGeometry.create(screenMultipolygon)
                        this.zoom = zoom
                    }
                    
                    theEntity.tryGet<ScaleComponent>()?.let {
                        it.zoom = zoom
                        it.scale = 1.0
                    }
                }
            }
    }

    protected override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewProjection = context.mapRenderContext.viewProjection

        if (camera().isIntegerZoom) {
            getEntities(COMPONENT_TYPES).forEach {
                it.setComponent(
                    MicroThreadComponent(
                        createScalingTask(it, viewProjection.zoom),
                        myQuantIterations
                    )
                )
            }
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            ZoomChangedComponent::class,
            WorldOriginComponent::class,
            WorldGeometryComponent::class,
            Components.ScreenOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}
