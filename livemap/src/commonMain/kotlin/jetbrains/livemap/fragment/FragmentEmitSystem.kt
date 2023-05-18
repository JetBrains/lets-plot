/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragment

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.datalore.base.typedGeometry.reinterpret
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.core.Transforms
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.flatMap
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.fragment.Utils.RegionsIndex
import jetbrains.livemap.fragment.Utils.entityName
import jetbrains.livemap.geometry.MicroTasks
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.camera.CameraListenerComponent
import jetbrains.livemap.mapengine.camera.CenterChangedComponent
import jetbrains.livemap.mapengine.camera.ZoomFractionChangedComponent
import jetbrains.livemap.mapengine.camera.ZoomLevelChangedComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.ViewportGridStateComponent

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
        val zoomProjection = Transforms.zoom<World, Client>(fragmentKey::zoom)

        val projector = MicroTasks
            .resample(boundaries, mapProjection::apply)
            .flatMap { worldMultiPolygon: MultiPolygon<World> ->
                val bbox = worldMultiPolygon.bbox ?: error("Fragment bbox can't be null")
                runLaterBySystem(
                    fragmentEntity
                ) { theEntity ->
                    theEntity
                        .addComponents {
                            +WorldDimensionComponent(bbox.dimension)
                            +WorldOriginComponent(bbox.origin)
                        }
                }
                MicroTasks.transform(worldMultiPolygon) { p -> zoomProjection.apply(p - bbox.origin) }
            }
            .map { screenMultiPolygon ->
                runLaterBySystem(
                    fragmentEntity
                ) { theEntity ->
                    theEntity
                        .addComponents {
                            + CameraListenerComponent()
                            + CenterChangedComponent()
                            + ZoomLevelChangedComponent()
                            + ZoomFractionChangedComponent()
                            + FragmentComponent(fragmentKey)
                            + ScreenGeometryComponent().apply { geometry = Geometry.of(screenMultiPolygon) }
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
