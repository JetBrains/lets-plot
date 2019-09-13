package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.AnyPoint
import jetbrains.datalore.base.projectionGeometry.Typed

class WorldProjection(
    zoom: Int
) : Transform<WorldPoint, ClientPoint> {
    private fun toDoubleVector(p: AnyPoint): DoubleVector {
        return DoubleVector(p.x, p.y)
    }

    override fun project(v: WorldPoint): ClientPoint {
        return projector.project(toDoubleVector(v)).let {
            Typed.Point(
                it.x,
                it.y
            )
        }
    }

    override fun invert(v: ClientPoint): WorldPoint {
        return projector.invert(toDoubleVector(v)).let {
            Typed.Point(
                it.x,
                it.y
            )
        }
    }

    private val projector =
        ProjectionUtil.square(ProjectionUtil.zoom(zoom))

}