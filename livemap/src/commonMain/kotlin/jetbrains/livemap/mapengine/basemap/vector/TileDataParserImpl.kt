/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.minus
import org.jetbrains.letsPlot.gis.tileprotocol.TileGeometryParser
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.core.Transforms
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.core.multitasking.flatMap
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.geometry.MicroTasks
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.viewport.CellKey

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

    private fun calculateTransform(cellKey: CellKey): (Vec<LonLat>) -> Vec<Client>? {
        val zoomProjection = Transforms.zoom<World, Client>(cellKey::length)
        val cellMapRect = cellKey.computeRect(myMapProjection.mapRect)
        val cellViewOrigin = zoomProjection.apply(cellMapRect.origin)

        return { lonLatVec -> myMapProjection.apply(lonLatVec)?.let { zoomProjection.apply(it) - cellViewOrigin } }
    }

    private fun parseTileLayer(
        tileLayer: TileLayer,
        transform: (Vec<LonLat>) -> Vec<Client>?
    ): MicroTask<List<TileFeature>> {
        return createMicroThread(TileGeometryParser(tileLayer.geometryCollection))
            .flatMap { tileGeometries ->
                val tileFeatures = ArrayList<TileFeature>()
                val microThreads = ArrayList<MicroTask<Unit>>()

                repeat(tileGeometries.size) {
                    val geometry = tileGeometries[it]
                    microThreads.add(
                        MicroTasks.resample(geometry, transform)
                            .map { worldMultiPolygon: Geometry<Client> ->
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