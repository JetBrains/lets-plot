package jetbrains.livemap.tiles

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileRect
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.gis.tileprotocol.TileGeometryParser
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.entities.geometry.GeometryTransform
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil

internal class TileDataParserImpl(private val myMapProjection: MapProjection) : TileDataParser {

    override fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>> {
        val transform = calculateTransform(cellKey)

        val result = HashMap<String, List<TileFeature>>()
        val microThreads = ArrayList<MicroTask<Unit>>()
        tileData.forEach { tileLayer ->
            microThreads.add(
                parseTileLayer(tileLayer, transform)
                    .map { result[tileLayer.name] = it }
            )
        }

        return MicroTaskUtil.join(microThreads).map { result }
    }

    private fun calculateTransform(cellKey: CellKey): (DoubleVector) -> DoubleVector {
        val cellMapRect = getTileRect(myMapProjection.mapRect, cellKey.toString())
        val zoom = cellKey.toString().length

        val zoomProjection = ProjectionUtil.square(ProjectionUtil.zoom(zoom))
        val cellViewOrigin = zoomProjection.project(cellMapRect.origin)

        return { zoomProjection.project(myMapProjection.project(it)).subtract(cellViewOrigin) }
    }

    private fun parseTileLayer(
        tileLayer: TileLayer,
        transform: (DoubleVector) -> DoubleVector
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
                                    emptyOrValue(tileLayer.kinds, it),
                                    emptyOrValue(tileLayer.subs, it),
                                    emptyOrValue(tileLayer.labels, it),
                                    emptyOrValue(tileLayer.shorts, it)
                                )
                            )

                            return@map
                        }
                    )
                }

                MicroTaskUtil.join(microThreads).map<List<TileFeature>> { tileFeatures }
            }
    }

    private fun <T> emptyOrValue(list: List<T>, index: Int): T? {
        return if (list.isEmpty()) null else list[index]
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