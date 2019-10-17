package jetbrains.datalore.jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.livemap.entities.geometry.GeometryTransform
import jetbrains.livemap.projections.ProjectionType
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.projections.ProjectionUtil.transformMultiPolygon
import jetbrains.livemap.projections.World
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class MultiPolygonTransformTest {

    private fun <TypeT> p(x: Double, y: Double): Vec<TypeT> {
        return explicitVec(x, y)
    }

    private fun <TypeT> multiPolygon(vararg polygons: Polygon<TypeT>): MultiPolygon<TypeT> {
        return MultiPolygon(polygons.asList())
    }

    private fun <TypeT> polygon(vararg rings: Ring<TypeT>): Polygon<TypeT> {
        return Polygon(rings.asList())
    }

    private fun <TypeT> ring(vararg points: Vec<TypeT>): Ring<TypeT> {
        return Ring(points.asList())
    }

    @Test
    fun transformTest() {
        val transform = GeometryTransform.simple<LonLat, LonLat>(
            multiPolygon(
                polygon(
                    ring(
                        p(10.0, 0.0),
                        p(10.0, 10.0),
                        p(10.0, 20.0),
                        p(10.0, 30.0)
                    ),
                    ring(
                        p(20.0, 0.0),
                        p(20.0, 10.0),
                        p(20.0, 20.0),
                        p(20.0, 30.0)
                    )
                ),

                polygon(
                    ring(
                        p(30.0, 0.0),
                        p(30.0, 10.0),
                        p(30.0, 20.0),
                        p(30.0, 30.0)
                    ),
                    ring(
                        p(40.0, 0.0),
                        p(40.0, 10.0),
                        p(40.0, 20.0),
                        p(40.0, 30.0)
                    )
                )
            )
        ) { it + explicitVec(1.0, 1.0) }

        var i = 0
        while (transform.alive()) {
            i++
            transform.resume()
        }

        assertEquals(17, i)
        assertEquals(
            multiPolygon(
                polygon(
                    ring(
                        p(11.0, 1.0),
                        p(11.0, 11.0),
                        p(11.0, 21.0),
                        p(11.0, 31.0)
                    ),
                    ring(
                        p(21.0, 1.0),
                        p(21.0, 11.0),
                        p(21.0, 21.0),
                        p(21.0, 31.0)
                    )
                ),

                polygon(
                    ring(
                        p(31.0, 1.0),
                        p(31.0, 11.0),
                        p(31.0, 21.0),
                        p(31.0, 31.0)
                    ),
                    ring(
                        p(41.0, 1.0),
                        p(41.0, 11.0),
                        p(41.0, 21.0),
                        p(41.0, 31.0)
                    )
                )
            ),
            transform.getResult()
        )
    }

    @Test
    fun resamplingTransform() {

        val input = multiPolygon<LonLat>(
            polygon(
                ring(
                    p(-153.98438, 56.55948),
                    p(-151.33594, 54.55948),
                    p(-151.16016, 58.65623),
                    p(-153.98438, 56.55948)
                ),
                ring(
                    p(123.98438, 26.55948),
                    p(121.33594, 24.55948),
                    p(121.16016, 28.65623),
                    p(123.98438, 26.55948)
                )
            ),
            polygon(
                ring(
                    p(-53.98438, 46.55948),
                    p(-51.33594, 44.55948),
                    p(-51.16016, 48.65623),
                    p(-53.98438, 46.55948)
                ),
                ring(
                    p(13.98438, 36.55948),
                    p(11.33594, 34.55948),
                    p(11.16016, 38.65623),
                    p(13.98438, 36.55948)
                )
            )
        )

        val mapProjection = createMapProjection(ProjectionType.MERCATOR, Rect(0.0, 0.0, 800.0, 600.0))
        val transform = GeometryTransform.resampling(input, mapProjection::project)

        while (transform.alive()) {
            transform.resume()
        }

        val xyMultipolygon = ArrayList<Polygon<World>>()
        val xyBoundary = transformMultiPolygon(input, mapProjection::project)
        for (xyPolygon in xyBoundary) {
            if (!xyPolygon.isEmpty()) {
                xyMultipolygon.add(xyPolygon)
            }
        }
        val expected = MultiPolygon(xyMultipolygon)

        assertEquals(expected, transform.getResult())
    }
}