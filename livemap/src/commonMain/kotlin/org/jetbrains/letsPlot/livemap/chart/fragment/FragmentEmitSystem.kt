/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isOnBorder
import org.jetbrains.letsPlot.livemap.chart.fragment.Utils.RegionsIndex
import org.jetbrains.letsPlot.livemap.chart.fragment.Utils.entityName
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.*
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
        val clipRect = fragmentKey.quadKey.computeRect()
        val clipPath = clipRect.toMultiPolygon()
        val inflatedClipRect = clipRect.inflate(Vec(clipRect.dimension.x * 0.125, clipRect.dimension.y * 0.125))

        val projector = FilterBorderMicroTask(boundaries, inflatedClipRect).flatMap { border ->
            MicroTaskUtil.pair(
                if (border.isEmpty()) {
                    MicroTaskUtil.constant(MultiLineString(emptyList()))
                } else {
                    MicroTasks.resample(border, mapProjection::apply)
                },
                MicroTaskUtil.pair(
                    MicroTasks.resample(clipPath, mapProjection::apply),
                    MicroTasks.resample(boundaries, mapProjection::apply)
                )
            )
        }.map { (worldMultiLineString, pair) ->
            val (worldClipPath, worldMultiPolygon) = pair
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

    private class FilterBorderMicroTask<T>(
        multiPolygon: MultiPolygon<T>,
        private val clipRect: Rect<T>
    ) : MicroTask<MultiLineString<T>> {
        private lateinit var polygonIterator: Iterator<Polygon<T>>
        private lateinit var ringIterator: Iterator<Ring<T>>
        private lateinit var pointIterator: Iterator<Vec<T>>

        private var newLineString: MutableList<Vec<T>> = ArrayList()
        private val newMultiLineString: MutableList<LineString<T>> = ArrayList()

        private var prevVisible = false
        private var prev: Vec<T>? = null

        private var hasNext = true
        private lateinit var result: MultiLineString<T>

        init {
            try {
                polygonIterator = multiPolygon.iterator()
                ringIterator = polygonIterator.next().iterator()
                pointIterator = ringIterator.next().iterator()
            } catch (e: RuntimeException) {
                println(e)
            }
        }

        override fun resume() {
            if (!pointIterator.hasNext()) {
                if (newLineString.isNotEmpty()) {
                    newMultiLineString.add(LineString(newLineString))
                }
                newLineString = mutableListOf()
                prev = null
                prevVisible = false

                if (!ringIterator.hasNext()) {
                    if (!polygonIterator.hasNext()) {
                        hasNext = false
                        result = MultiLineString(newMultiLineString)
                        return
                    } else {
                        ringIterator = polygonIterator.next().iterator()
                        pointIterator = ringIterator.next().iterator()
                        newLineString = ArrayList()
                    }
                } else {
                    pointIterator = ringIterator.next().iterator()
                    newLineString = ArrayList()
                }
            }
            val currentPoint = pointIterator.next()

            if (currentPoint.isOnBorder(clipRect)) {
                if (prevVisible) {
                    newLineString.add(currentPoint)

                    if (newLineString.isNotEmpty()) {
                        newMultiLineString.add(LineString(newLineString))
                    }
                    newLineString = mutableListOf()
                }

                prev = currentPoint
                prevVisible = false
            } else {
                if (!prevVisible && prev != null) {
                    newLineString.add(prev!!)
                }
                newLineString.add(currentPoint)
                prevVisible = true
            }
        }

        override fun alive(): Boolean {
            return hasNext
        }

        override fun getResult(): MultiLineString<T> {
            return result
        }
    }
}
