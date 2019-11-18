/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.minus
import jetbrains.datalore.base.projectionGeometry.reinterpret
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.GeometryUtil.bbox
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.CameraListenerComponent
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.Utils.common
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.flatMap
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.geometry.GeometryTransform
import jetbrains.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.entities.regions.Utils.RegionsIndex
import jetbrains.livemap.entities.regions.Utils.entityName
import jetbrains.livemap.entities.regions.Utils.zoom
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil
import jetbrains.livemap.projections.World
import jetbrains.livemap.tiles.components.CellStateComponent

class FragmentEmitSystem(private val myProjectionQuant: Int, componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {
    private val myRegionIndex: RegionsIndex = RegionsIndex(componentManager)
    private val myWaitingForScreenGeometry = HashMap<FragmentKey, EcsEntity>()

    override fun initImpl(context: LiveMapContext) {
        createEntity("FragmentsFetch")
            .addComponent(StreamingFragmentsComponent())
            .addComponent(EmittedFragmentsComponent())
            .addComponent(CachedFragmentsComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val downloaded = getSingleton<DownloadingFragmentsComponent>().downloaded

        val emptyFragments = HashSet<FragmentKey>()
        if (downloaded.isNotEmpty()) {
            val visibleQuads = getSingleton<CellStateComponent>().visibleQuads

            val processing = HashSet<QuadKey<LonLat>>()
            val obsolete = HashSet<QuadKey<LonLat>>()

            downloaded.forEach { (fragmentKey, geometry) ->
                if (!visibleQuads.contains(fragmentKey.quadKey)) {
                    // too slow. received geometry is already obsolete. Do not create entity and geometry processing.
                    getSingleton<StreamingFragmentsComponent>().remove(fragmentKey)
                    obsolete.add(fragmentKey.quadKey)
                } else if (!geometry.isEmpty()) {
                    processing.add(fragmentKey.quadKey)
                    myWaitingForScreenGeometry.put(
                        fragmentKey,
                        createFragmentEntity(fragmentKey, geometry.reinterpret(), context.mapProjection)
                    )
                } else {
                    // No geometry - stop waiting for this fragment
                    emptyFragments.add(fragmentKey)
                    getSingleton<StreamingFragmentsComponent>().remove(fragmentKey)
                }
            }
        }

        val transformedFragments = findTransformedFragments()
        transformedFragments.forEach { (fragmentKey, fragmentEntity) ->
            getSingleton<StreamingFragmentsComponent>().remove(fragmentKey)
            getSingleton<CachedFragmentsComponent>().store(fragmentKey, fragmentEntity)
        }

        val emittedFragments = HashSet<FragmentKey>()
        emittedFragments.addAll(emptyFragments)
        emittedFragments.addAll(transformedFragments.keys)
        emittedFragments.addAll(
            common(
                getSingleton<ChangedFragmentsComponent>().requested,
                getSingleton<CachedFragmentsComponent>().keys()
            )
        )

        getSingleton<EmptyFragmentsComponent>().addAll(emptyFragments)
        getSingleton<EmittedFragmentsComponent>().setEmitted(emittedFragments)
    }

    private fun findTransformedFragments(): Map<FragmentKey, EcsEntity> {
        val transformedFragments = HashMap<FragmentKey, EcsEntity>()

        val it = myWaitingForScreenGeometry.values.iterator()
        while (it.hasNext()) {
            val fragmentEntity = it.next()
            if (fragmentEntity.contains(ScreenGeometryComponent::class)) {
                transformedFragments[fragmentEntity.get<FragmentComponent>().fragmentKey] = fragmentEntity
                it.remove()
            }
        }

        return transformedFragments
    }

    private fun createFragmentEntity(
        fragmentKey: FragmentKey,
        boundaries: MultiPolygon<LonLat>,
        mapProjection: MapProjection
    ): EcsEntity {
        checkArgument(!boundaries.isEmpty())

        val fragmentEntity = createEntity(entityName(fragmentKey))
        val zoomProjection = ProjectionUtil.square<World, Client>(ProjectionUtil.zoom(zoom(fragmentKey)))

        val projector = GeometryTransform
            .resampling(boundaries, mapProjection::project)
            .flatMap { worldMultiPolygon: MultiPolygon<World> ->
                val bbox = bbox(worldMultiPolygon) ?: error("")
                runLaterBySystem(
                    fragmentEntity
                ) { theEntity ->
                    theEntity
                        .addComponent(WorldDimensionComponent(bbox.dimension))
                        .addComponent(WorldOriginComponent(bbox.origin))
                }
                GeometryTransform.simple(worldMultiPolygon) { p -> zoomProjection.project(p - bbox.origin) }
            }
            .map { screenMultiPolygon ->
                runLaterBySystem(
                    fragmentEntity
                ) { theEntity ->
                    theEntity
                        .addComponent(CameraListenerComponent())
                        .addComponent(CenterChangedComponent())
                        .addComponent(ZoomChangedComponent())
                        .addComponent(ScaleComponent().apply { zoom = zoom(fragmentKey) })
                        .addComponent(FragmentComponent(fragmentKey))
                        .addComponent(ScreenLoopComponent())
                        .addComponent(ScreenGeometryComponent().apply { geometry = screenMultiPolygon })
                        .addComponent(myRegionIndex.find(fragmentKey.regionId).get<ParentLayerComponent>())
                }
                return@map
            }

        fragmentEntity.addComponent(MicroThreadComponent(projector, myProjectionQuant))
        getSingleton<StreamingFragmentsComponent>()[fragmentKey] = fragmentEntity
        return fragmentEntity
    }
}