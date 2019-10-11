package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Iterables.get
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.PI

/**
 * This is working code but wasn't included to ggplot.
 * Mostly exists to preserve this code.
 */
internal class PieGeom(private val myCenter: DoubleVector, private val myRadius: Double) : GeomBase() {

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = PieHelper(pos, coord, ctx)
        val segments = helper.createSegments(aesthetics, myCenter, myRadius)
        appendNodes(segments, root)
    }

    private class PieHelper internal constructor(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) : LinesHelper(pos, coord, ctx) {

        internal fun createSegments(aesthetics: Aesthetics, center: DoubleVector, radius: Double): List<LinePath> {
            val result = ArrayList<LinePath>()

            //DoubleVector basis = new Point(radius, 0);  // x
            val basis = DoubleVector(0.0, -radius)   // y

            var curAngle = Double.NaN
            for (p in dataPoints(aesthetics)) {
                val segmentSize = shareToRad(p.y()!!)

                // for better presentation: first segment lays to the right of 12 o'clock, and all
                // segments are added counterclockwise.
                if (curAngle.isNaN()) {
                    curAngle = segmentSize
                }

                // we use negative angles because of screen coordinates and counterclockwise order of segments
                val angle = -segmentSize

                val builder = SvgPathDataBuilder(true)
                builder.moveTo(center)
                builder.lineTo(center.add(basis.rotate(curAngle)))
                val arcTo = center.add(basis.rotate(curAngle + angle))
                builder.ellipticalArc(radius, radius, 0.0, -angle > PI, false, arcTo)
                builder.closePath()

                curAngle += angle

                val path = LinePath(builder)
                decorate(path, p, true)
                result.add(path)
            }

            return result
        }
    }

    companion object {
        val RENDERS = listOf(
                Aes.X, // optional, can specify order of segments in pie
                Aes.Y, // angle width of segments
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.WIDTH,
                Aes.SIZE
        )

        const val HANDLES_GROUPS = false

        private fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
            val withX = GeomUtil.with_X_Y(aesthetics.dataPoints())
            return if (Iterables.isEmpty(withX) || allEqualX(
                    withX,
                    get(withX, 0).x()
                )
            ) {
                GeomUtil.ordered_Y(GeomUtil.with_Y(aesthetics.dataPoints()), true)
            } else GeomUtil.ordered_X(withX)
        }

        private fun allEqualX(hasX: Iterable<DataPointAesthetics>, `val`: Double?): Boolean {
            for (p in hasX) {
                if (p.x() != `val`) {
                    return false
                }
            }
            return true
        }

        private fun shareToRad(share: Double): Double {
            // don't allow sum to be more than 99.99 % of full circle (otherwise arc will disappear)
            return 0.9999 * 2.0 * PI * share
        }
    }
}
