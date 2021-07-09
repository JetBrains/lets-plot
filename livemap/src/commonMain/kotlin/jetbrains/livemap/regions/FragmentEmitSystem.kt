/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.regions

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.datalore.base.typedGeometry.reinterpret
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.CameraListenerComponent
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.viewport.ViewportGridStateComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.flatMap
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.core.projections.ProjectionUtil
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.geometry.GeometryTransform
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.placement.WorldDimensionComponent
import jetbrains.livemap.placement.WorldOriginComponent
import jetbrains.livemap.projection.Client
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.projection.World
import jetbrains.livemap.regions.Utils.RegionsIndex
import jetbrains.livemap.regions.Utils.entityName
import jetbrains.livemap.regions.Utils.zoom
import jetbrains.livemap.scaling.ScaleComponent

class FragmentEmitSystem(
    private val myProjectionQuant: Int,
    componentManager: EcsComponentManager
) :
    AbstractSystem<LiveMapContext>(componentManager) {
    private val myRegionIndex: RegionsIndex = RegionsIndex(componentManager)
    private val myWaitingForScreenGeometry = HashMap<FragmentKey, EcsEntity>()

    override fun initImpl(context: LiveMapContext) {
        createEntity("FragmentsFetch")
            .addComponents {
                + StreamingFragmentsComponent()
                + EmittedFragmentsComponent()
                + CachedFragmentsComponent()
            }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val downloaded = getSingleton<DownloadingFragmentsComponent>().downloaded

        val emptyFragments = HashSet<FragmentKey>()
        if (downloaded.isNotEmpty()) {
            val visibleQuads = getSingleton<ViewportGridStateComponent>().visibleQuads

            val processing = HashSet<QuadKey<LonLat>>()
            val obsolete = HashSet<QuadKey<LonLat>>()

            downloaded.forEach { (fragmentKey, geometry) ->
                if (!visibleQuads.contains(fragmentKey.quadKey)) {
                    // too slow. received geometry is already obsolete. Do not create entity and geometry processing.
                    getSingleton<StreamingFragmentsComponent>().remove(fragmentKey)
                    obsolete.add(fragmentKey.quadKey)
                } else if (!geometry.isEmpty()) {
                    processing.add(fragmentKey.quadKey)
                    myWaitingForScreenGeometry[fragmentKey] =
                        createFragmentEntity(fragmentKey, geometry.reinterpret(), context.mapProjection)
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
            getSingleton<ChangedFragmentsComponent>().requested.intersect(
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
        require(!boundaries.isEmpty())

        val fragmentEntity = createEntity(entityName(fragmentKey))
        val zoomProjection = ProjectionUtil.square<World, Client>(ProjectionUtil.zoom(zoom(fragmentKey)))

        val projector = GeometryTransform
            .resampling(boundaries, mapProjection::project)
            .flatMap { worldMultiPolygon: MultiPolygon<World> ->
                val bbox = GeometryUtil.bbox(worldMultiPolygon) ?: error("Fragment bbox can't be null")
                runLaterBySystem(
                    fragmentEntity
                ) { theEntity ->
                    theEntity
                        .addComponents {
                            + WorldDimensionComponent(bbox.dimension)
                            + WorldOriginComponent(bbox.origin)
                        }
                }
                GeometryTransform.simple(worldMultiPolygon) { p -> zoomProjection.project(p - bbox.origin) }
            }
            .map { screenMultiPolygon ->
                runLaterBySystem(
                    fragmentEntity
                ) { theEntity ->
                    theEntity
                        .addComponents {
                            + CameraListenerComponent()
                            + CenterChangedComponent()
                            + ZoomChangedComponent()
                            + ScaleComponent().apply { zoom = zoom(fragmentKey) }
                            + FragmentComponent(fragmentKey)
                            + ScreenLoopComponent()
                            + ScreenGeometryComponent().apply { geometry = screenMultiPolygon }
                            + myRegionIndex.find(fragmentKey.regionId).get<ParentLayerComponent>()
                        }
                }
                return@map
            }

        fragmentEntity.add(MicroThreadComponent(projector, myProjectionQuant))
        getSingleton<StreamingFragmentsComponent>()[fragmentKey] = fragmentEntity
        return fragmentEntity
    }
}