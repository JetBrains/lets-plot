package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.center
import jetbrains.datalore.base.projectionGeometry.height
import jetbrains.datalore.base.projectionGeometry.width
import jetbrains.livemap.projections.ProjectionUtil.transformBBox
import kotlin.math.min

internal class MapProjectionBuilder(
    private val geoProjection: GeoProjection,
    private val mapRect: WorldRectangle
) {
    private var reverseX = false
    private var reverseY = false

    fun reverseX(): MapProjectionBuilder {
        reverseX = true
        return this
    }

    fun reverseY(): MapProjectionBuilder {
        reverseY = true
        return this
    }

    fun create(): MapProjection {
        val rect = transformBBox(geoProjection.validRect(), geoProjection::project)

        val scale = min(mapRect.width / rect.width, mapRect.height / rect.height)

        val projSize = mapRect.dimension.mul(1.0 / scale)
        val projRect = DoubleRectangle(rect.center.subtract(projSize.mul(0.5)), projSize)

        val offsetX = if (reverseX) projRect.right else projRect.left
        val scaleX = if (reverseX) -scale else scale
        val offsetY = if (reverseY) projRect.bottom else projRect.top
        val scaleY = if (reverseY) -scale else scale

        val linearProjection = ProjectionUtil.tuple(
            ProjectionUtil.linear(offsetX, scaleX),
            ProjectionUtil.linear(offsetY, scaleY)
        )

        val proj = ProjectionUtil.composite(geoProjection, linearProjection)

        return object : MapProjection {

            override val mapRect: DoubleRectangle
                get() = this@MapProjectionBuilder.mapRect

            override fun project(v: LonLatPoint): WorldPoint {
                return proj.project(v).toWorldPoint()
            }

            override fun invert(v: WorldPoint): LonLatPoint {
                return proj.invert(v)
            }
        }
    }
}