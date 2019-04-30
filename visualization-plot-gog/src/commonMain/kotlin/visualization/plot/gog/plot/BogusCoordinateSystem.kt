package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem

internal class BogusCoordinateSystem : CoordinateSystem {
    override fun toClient(p: DoubleVector): DoubleVector {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }
}
