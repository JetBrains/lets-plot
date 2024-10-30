/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.within
import org.jetbrains.letsPlot.livemap.chart.fragment.Utils.RegionsIndex
import org.jetbrains.letsPlot.livemap.chart.fragment.Utils.entityName
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskUtil
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroThreadComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.map
import org.jetbrains.letsPlot.livemap.geometry.MicroTasks
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridStateComponent

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
            if (fragmentEntity.contains(WorldGeometryComponent::class)) {
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
        val clipPath = fragmentKey.quadKey.computeRect().toMultiPolygon()

        val projector = MicroTaskUtil.pair(
            MicroTasks.resample(clipPath, mapProjection::apply),
            MicroTasks.resample(boundaries, mapProjection::apply)
        ).map { (worldClipPath, worldMultiPolygon) ->
            Triple(worldMultiPolygon, worldClipPath, worldMultiPolygon.clipBorder(worldClipPath))
        }.map { (worldMultiPolygon, worldClipPath, worldMultiLineString) ->
            val bbox = worldMultiPolygon.bbox ?: error("Fragment bbox can't be null")

            runLaterBySystem(fragmentEntity) { theEntity ->
                theEntity
                    .addComponents {
                        + WorldDimensionComponent(bbox.dimension)
                        + WorldOriginComponent(bbox.origin)
                        + WorldGeometryComponent().apply { geometry = Geometry.of(worldMultiPolygon) }
                        + FragmentComponent(fragmentKey, worldClipPath, worldMultiLineString)
                        + myRegionIndex.find(fragmentKey.regionId).get<ParentLayerComponent>()
                    }
            }
        }

        fragmentEntity.add(MicroThreadComponent(projector, myProjectionQuant))
        getSingleton<StreamingFragmentsComponent>()[fragmentKey] = fragmentEntity
        return fragmentEntity
    }

    fun <T> MultiPolygon<T>.clipBorder(clipPath: MultiPolygon<T>): MultiLineString<T> {
        val result = mutableListOf<LineString<T>>()
        for (polygon in this) {
            for (ring in polygon) {
                var line = mutableListOf<Vec<T>>()
                var prevVisible = false
                var prev = ring[0]

                ring.forEach {
                    if (it.within(clipPath)) {
                        if (!prevVisible) {
                            line.add(prev)
                        }
                        line.add(it)
                        prevVisible = true
                    } else {
                        if (prevVisible) {
                            line.add(it)
                            result.add(LineString(line))
                            line = mutableListOf()
                        }
                        prev = it
                        prevVisible = false
                    }
                }
                if (line.isNotEmpty()) {
                    result.add(LineString(line))
                }
            }
        }

        return MultiLineString(result)
    }
}
