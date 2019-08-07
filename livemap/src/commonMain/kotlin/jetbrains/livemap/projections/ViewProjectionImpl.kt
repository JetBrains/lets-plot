package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.MapWidgetUtil.MAX_ZOOM
import jetbrains.livemap.MapWidgetUtil.MIN_ZOOM
import kotlin.math.max
import kotlin.math.min

internal class ViewProjectionImpl(
    private val helper: ViewProjectionHelper,
    override val viewSize: DoubleVector,
    private var viewCenter: DoubleVector
) : ViewProjection {
    private var myZoom: Int = 1

    override val visibleCells: Set<CellKey>
        get() = helper.getCells(viewRect, myZoom)

    override var zoom: Int
        get() = myZoom
        set(zoom) {
            myZoom = normalizeZoom(zoom)
        }

    override val viewRect: DoubleRectangle
        get() {
            val mapViewSize = unzoom(viewSize)
            val mapOrigin = viewCenter.subtract(mapViewSize.mul(0.5))
            return DoubleRectangle(mapOrigin, mapViewSize)
        }

    override var center: DoubleVector
        get() = viewCenter
        set(center) {
            viewCenter = normalize(center)
        }

    private fun normalizeZoom(zoom: Int): Int {
        return max(MIN_ZOOM, min(zoom, MAX_ZOOM))
    }

    override fun getViewX(mapX: Double): Double {
        return zoom(mapX - viewCenter.x) + viewSize.x / 2.0
    }

    override fun getViewY(mapY: Double): Double {
        return zoom(mapY - viewCenter.y) + viewSize.y / 2.0
    }

    override fun getMapX(viewX: Double): Double {
        return helper.normalizeX(invertX(viewX))
    }

    override fun getMapY(viewY: Double): Double {
        return helper.normalizeY(invertY(viewY))
    }

    private fun invertX(viewX: Double): Double {
        return unzoom(viewX - viewSize.x / 2.0) + viewCenter.x
    }

    private fun invertY(viewY: Double): Double {
        return unzoom(viewY - viewSize.y / 2.0) + viewCenter.y
    }

    private fun invert(p: DoubleVector): DoubleVector {
        return DoubleVector(invertX(p.x), invertY(p.y))
    }

    override fun getOrigins(viewOrigin: DoubleVector, viewDimension: DoubleVector): List<DoubleVector> {
        val rect = DoubleRectangle.span(invert(viewOrigin), invert(viewOrigin.add(viewDimension)))

        val result = ArrayList<DoubleVector>()
        helper.getOrigins(rect, viewRect).forEach { point -> result.add(getViewCoord(point)) }
        return result
    }

    private fun zoom(coord: Double): Double {
        return coord * (1 shl myZoom)
    }

    private fun unzoom(coord: Double): Double {
        return coord / (1 shl myZoom)
    }

    private fun unzoom(v: DoubleVector): DoubleVector {
        return DoubleVector(unzoom(v.x), unzoom(v.y))
    }

    private fun normalize(v: DoubleVector): DoubleVector {
        return DoubleVector(helper.normalizeX(v.x), helper.normalizeY(v.y))
    }
}