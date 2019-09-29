package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.minus
import jetbrains.datalore.maps.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.datalore.maps.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.coroutine.MicroCoThreadComponent
import jetbrains.livemap.core.multitasking.coroutine.microCoThread
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import jetbrains.livemap.entities.geometry.MultiPolygonTransformCoroutine.transform
import jetbrains.livemap.entities.placement.ScreenOriginComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.WorldProjection


class WorldGeometry2ScreenUpdateSystem(
    private val myQuantIterations: Int,
    componentManager: EcsComponentManager
) : LiveMapSystem(componentManager) {

    private fun createScalingMicroThread(entity: EcsEntity, zoom: Int): MicroCoThreadComponent {

        return microCoThread {
            // Fix ghosting after zoom by removing outdated screen geometry.
            if (!entity.contains<ScaleComponent>()) {
                entity.remove<ScreenGeometryComponent>()
            }

            val worldOrigin = entity.get<WorldOriginComponent>().origin
            val zoomProjection = WorldProjection(zoom)
            val worldGeometry = entity.get<WorldGeometryComponent>().geometry!!.asMultipolygon()

            val screenGeometry =
                transform(worldGeometry) { p, o: MutableCollection<Vec<Client>> -> o.add(zoomProjection.project(p - worldOrigin)) }

            runLaterBySystem(entity) { theEntity ->
                tagDirtyParentLayer(theEntity)
                theEntity.provide(::ScreenGeometryComponent).apply {
                    geometry = ClientGeometry.create(screenGeometry)
                    this.zoom = zoom
                }

                theEntity.tryGet<ScaleComponent>()?.let {
                    it.zoom = zoom
                    it.scale = 1.0
                }
            }
        }
    }

    private fun createScalingTask(entity: EcsEntity, zoom: Int): MicroThreadComponent {
        // Fix ghosting after zoom by removing outdated screen geometry.
        if (!entity.contains(ScaleComponent::class)) {
            entity.remove<ScreenGeometryComponent>()
        }

        val worldOrigin = entity.get<WorldOriginComponent>().origin
        val zoomProjection = WorldProjection(zoom)

        return MicroThreadComponent(GeometryTransform
            .simple(entity.get<WorldGeometryComponent>().geometry!!.asMultipolygon()) {
                zoomProjection.project(it - worldOrigin)
            }
            .map { screenMultipolygon ->
                runLaterBySystem(entity) { theEntity ->
                    tagDirtyParentLayer(theEntity)
                    theEntity.provide(::ScreenGeometryComponent).apply {
                        geometry = ClientGeometry.create(screenMultipolygon)
                        this.zoom = zoom
                    }

                    theEntity.tryGet<ScaleComponent>()?.let {
                        it.zoom = zoom
                        it.scale = 1.0
                    }
                }
            },
            myQuantIterations
        )
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewProjection = context.mapRenderContext.viewProjection

        if (camera().isIntegerZoom) {
            getEntities(COMPONENT_TYPES).forEach {
                it.setComponent(createScalingMicroThread(it, viewProjection.zoom))
            }
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            ZoomChangedComponent::class,
            WorldOriginComponent::class,
            WorldGeometryComponent::class,
            ScreenOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}
