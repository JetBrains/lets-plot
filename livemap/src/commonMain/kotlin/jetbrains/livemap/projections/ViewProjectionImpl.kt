package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.livemap.MapWidgetUtil.MAX_ZOOM
import jetbrains.livemap.MapWidgetUtil.MIN_ZOOM
import kotlin.math.max
import kotlin.math.min

internal class ViewProjectionImpl(
    private val helper: ViewProjectionHelper,
    override val viewSize: ClientPoint,
    private var viewCenter: WorldPoint
) : ViewProjection {
    private var myZoom: Int = 1

    override val visibleCells: Set<CellKey>
        get() = helper.getCells(viewRect, myZoom)

    override var zoom: Int
        get() = myZoom
        set(zoom) {
            myZoom = normalizeZoom(zoom)
        }

    override val viewRect: WorldRectangle
        get() {
            val mapViewSize = unzoom(viewSize)
            val mapOrigin = viewCenter - mapViewSize / 2.0
            return WorldRectangle(mapOrigin, mapViewSize)
        }

    override var center: WorldPoint
        get() = viewCenter
        set(center) {
            viewCenter = normalize(center)
        }

    private fun normalizeZoom(zoom: Int): Int {
        return max(MIN_ZOOM, min(zoom, MAX_ZOOM))
    }

    override fun getViewX(p: WorldPoint): Double {
        return zoom(p.x - viewCenter.x) + viewSize.x / 2.0
    }

    override fun getViewY(p: WorldPoint): Double {
        return zoom(p.y - viewCenter.y) + viewSize.y / 2.0
    }

    override fun getMapX(p: ClientPoint): Double {
        return helper.normalizeX(invertX(p.x))
    }

    override fun getMapY(p: ClientPoint): Double {
        return helper.normalizeY(invertY(p.y))
    }

    private fun invertX(viewX: Double): Double {
        return unzoom(viewX - viewSize.x / 2.0) + viewCenter.x
    }

    private fun invertY(viewY: Double): Double {
        return unzoom(viewY - viewSize.y / 2.0) + viewCenter.y
    }

    private fun invert(p: ClientPoint): WorldPoint {
        return explicitVec<World>(invertX(p.x), invertY(p.y))
    }

    override fun getOrigins(viewOrigin: ClientPoint, viewDimension: ClientPoint): List<ClientPoint> {
        val rect =
            Rect(invert(viewOrigin), invert(viewOrigin + viewDimension))

        return helper.getOrigins(rect, viewRect).map { getViewCoord(it) }
    }

    private fun zoom(coord: Double): Double {
        return coord * (1 shl myZoom)
    }

    private fun unzoom(coord: Double): Double {
        return coord / (1 shl myZoom)
    }

    private fun unzoom(v: ClientPoint): WorldPoint {
        return explicitVec<World>(unzoom(v.x), unzoom(v.y))
    }

    private fun normalize(v: WorldPoint): WorldPoint {
        return explicitVec<World>(helper.normalizeX(v.x), helper.normalizeY(v.y))
    }
}