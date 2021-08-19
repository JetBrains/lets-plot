/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.computeRect
import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.gis.tileprotocol.TileGeometryParser
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.core.multitasking.flatMap
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.geometry.GeometryTransform
import jetbrains.livemap.projection.Client
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.projection.WorldProjection

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

    private fun calculateTransform(cellKey: CellKey): (Vec<LonLat>) -> Vec<Client> {
        val zoomProjection = WorldProjection(cellKey.length)
        val cellMapRect = cellKey.computeRect(myMapProjection.mapRect)
        val cellViewOrigin = zoomProjection.project(cellMapRect.origin)

        return { zoomProjection.project(myMapProjection.project(it)) - cellViewOrigin }
    }

    private fun parseTileLayer(
        tileLayer: TileLayer,
        transform: (Vec<LonLat>) -> Vec<Client>
    ): MicroTask<List<TileFeature>> {
        return createMicroThread(TileGeometryParser(tileLayer.geometryCollection))
            .flatMap { tileGeometries ->
                val tileFeatures = ArrayList<TileFeature>()
                val microThreads = ArrayList<MicroTask<Unit>>()

                repeat(tileGeometries.size) {
                    val geometry = tileGeometries[it]
                    microThreads.add(
                        GeometryTransform.resampling(geometry, transform)
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