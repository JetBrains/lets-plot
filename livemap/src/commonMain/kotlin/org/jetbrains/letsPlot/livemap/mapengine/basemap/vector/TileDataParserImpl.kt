/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.minus
import org.jetbrains.letsPlot.gis.tileprotocol.TileGeometryParser
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.Transforms
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskUtil
import org.jetbrains.letsPlot.livemap.core.multitasking.flatMap
import org.jetbrains.letsPlot.livemap.core.multitasking.map
import org.jetbrains.letsPlot.livemap.geometry.MicroTasks
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey

internal class TileDataParserImpl(private val myMapProjection: MapProjection) : TileDataParser {

    override fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>> {
        val transform = calculateTransform(cellKey)

        val result = HashMap<String, List<TileFeature>>()

        val microThreads = tileData
            .map { tileLayer -> parseTileLayer(tileLayer, transform)
                .map { result[tileLayer.name] = it }
        }

        return MicroTaskUtil.join(microThreads).map { result }
    }

    private fun calculateTransform(cellKey: CellKey): (Vec<LonLat>) -> Vec<org.jetbrains.letsPlot.livemap.Client>? {
        val zoomProjection = Transforms.zoom<org.jetbrains.letsPlot.livemap.World, org.jetbrains.letsPlot.livemap.Client>(cellKey::length)
        val cellMapRect = cellKey.computeRect(myMapProjection.mapRect)
        val cellViewOrigin = zoomProjection.apply(cellMapRect.origin)

        return { lonLatVec -> myMapProjection.apply(lonLatVec)?.let { zoomProjection.apply(it) - cellViewOrigin } }
    }

    private fun parseTileLayer(
        tileLayer: TileLayer,
        transform: (Vec<LonLat>) -> Vec<org.jetbrains.letsPlot.livemap.Client>?
    ): MicroTask<List<TileFeature>> {
        return createMicroThread(TileGeometryParser(tileLayer.geometryCollection))
            .flatMap { tileGeometries ->
                val tileFeatures = ArrayList<TileFeature>()
                val microThreads = ArrayList<MicroTask<Unit>>()

                repeat(tileGeometries.size) {
                    val geometry = tileGeometries[it]
                    microThreads.add(
                        MicroTasks.resample(geometry, transform)
                            .map { worldMultiPolygon: Geometry<org.jetbrains.letsPlot.livemap.Client> ->
                                tileFeatures.add(
                                    TileFeature(
                                        worldMultiPolygon,
                                        tileLayer.kinds.getOrNull(it),
                                        tileLayer.subs.getOrNull(it),
                                        tileLayer.labels.getOrNull(it),
                                        tileLayer.shorts.getOrNull(it)
                                    )
                                )
                                return@map
                            }
                    )
                }
                MicroTaskUtil
                    .join(microThreads)
                    .map<Unit, List<TileFeature>> { tileFeatures }
            }
    }

    private fun createMicroThread(tileGeometryParser: TileGeometryParser): MicroTask<List<Geometry<LonLat>>> {
        return object : MicroTask<List<Geometry<LonLat>>> {
            private var myDone = false

            override fun getResult(): List<Geometry<LonLat>> = tileGeometryParser.geometries

            override fun resume() {
                if (!tileGeometryParser.resume()) {
                    myDone = true
                }
            }

            override fun alive(): Boolean {
                return !myDone
            }
        }
    }
}