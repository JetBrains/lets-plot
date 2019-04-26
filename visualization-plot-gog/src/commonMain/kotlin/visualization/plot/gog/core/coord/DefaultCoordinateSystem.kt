package jetbrains.datalore.visualization.plot.gog.core.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem

internal class DefaultCoordinateSystem(private val myToClientOffsetX: (Double) -> Double, private val myToClientOffsetY: (Double) -> Double,
                                       private val myFromClientOffsetX: (Double) -> Double, private val myFromClientOffsetY: (Double) -> Double) : CoordinateSystem {

    override fun toClient(p: DoubleVector): DoubleVector {
        return DoubleVector(myToClientOffsetX(p.x), myToClientOffsetY(p.y))
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return DoubleVector(myFromClientOffsetX(p.x), myFromClientOffsetY(p.y))
    }
}
