package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem

class GeomCoord(private val myCoord: CoordinateSystem) {

    fun toClient(p: DoubleVector): DoubleVector {
        return myCoord.toClient(p)
    }

    internal fun fromClient(location: DoubleVector): DoubleVector {
        return myCoord.fromClient(location)
    }

    fun toClient(r: DoubleRectangle): DoubleRectangle {
        var r = r
        val xy1 = r.origin
        val xy2 = DoubleVector(r.right, r.bottom)

        val xy1_ = myCoord.toClient(xy1)
        val xy2_ = myCoord.toClient(xy2)
        if (xy1 != xy1_ || xy2 != xy2_) {
            val xMin = Math.min(xy1_.x, xy2_.x)
            val yMin = Math.min(xy1_.y, xy2_.y)
            val xMax = Math.max(xy1_.x, xy2_.x)
            val yMax = Math.max(xy1_.y, xy2_.y)

            r = DoubleRectangle(xMin, yMin, xMax - xMin, yMax - yMin)
        }
        return r
    }
}
