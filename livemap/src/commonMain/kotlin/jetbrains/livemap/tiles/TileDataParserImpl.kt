package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.LonLatPoint
import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.gis.tileprotocol.TileGeometryParser
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.entities.geometry.GeometryTransform
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.WorldProjection

internal class TileDataParserImpl(private val myMapProjection: MapProjection) : TileDataParser {

    override fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>> {
        val transform = calculateTransform(cellKey)

        val result = HashMap<String, List<TileFeature>>()

        val microThreads = tileData.map { tileLayer ->
            parseTileLayer(tileLayer, transform)
                .map { result[tileLayer.name] = it }
        }

        return MicroTaskUtil.join(microThreads).map { result }
    }

    private fun calculateTransform(cellKey: CellKey): (LonLatPoint) -> Typed.Point<Client> {
        val zoomProjection = WorldProjection(cellKey.length)
        val cellMapRect = getTileRect(myMapProjection.mapRect, cellKey.toString())
        val cellViewOrigin = zoomProjection.project(cellMapRect.origin)

        return { zoomProjection.project(myMapProjection.project(it)).subtract(cellViewOrigin) }
    }

    private fun parseTileLayer(
        tileLayer: TileLayer,
        transform: (LonLatPoint) -> Typed.Point<Client>
    ): MicroTask<List<TileFeature>> {
        return createMicroThread(TileGeometryParser(tileLayer.geometryCollection))
            .flatMap { tileGeometries ->
                val tileFeatures = ArrayList<TileFeature>()
                val microThreads = ArrayList<MicroTask<Unit>>()

                repeat(tileGeometries.size) {
                    val geometry = tileGeometries[it]
                    microThreads.add(
                        GeometryTransform.resampling(geometry, transform).map { worldMultiPolygon ->
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

                MicroTaskUtil.join(microThreads).map<List<TileFeature>> { tileFeatures }
            }
    }

    private fun createMicroThread(tileGeometryParser: TileGeometryParser): MicroTask<List<TileFeature.TileGeometry>> {
        return object : MicroTask<List<TileFeature.TileGeometry>> {
            private var myDone = false

            override fun getResult(): List<TileFeature.TileGeometry> = tileGeometryParser.geometries

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