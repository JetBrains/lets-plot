package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.projectionGeometry.GeoUtils.normalizeLon
import jetbrains.livemap.projections.MapRuler
import kotlin.math.*

object MapWidgetUtil {
    const val MIN_ZOOM = 1
    const val MAX_ZOOM = 15
    private const val LONGITUDE_EPS = 1.0
    private const val FULL_ANGLE = 360.0
    private const val STRAIGHT_ANGLE = 180.0

    fun splitPathByAntiMeridian(path: List<DoubleVector>): List<List<DoubleVector>> {
        val pathList = ArrayList<List<DoubleVector>>()
        var currentPath: MutableList<DoubleVector> = ArrayList<DoubleVector>()
        if (!path.isEmpty()) {
            currentPath.add(path[0])

            for (i in 1 until path.size) {
                val prev = path[i - 1]
                val next = path[i]
                val lonDelta = abs(next.x - prev.x)

                if (lonDelta > FULL_ANGLE - lonDelta) {
                    val sign = (if (prev.x < 0.0) -1 else +1).toDouble()

                    val x1 = prev.x - sign * STRAIGHT_ANGLE
                    val x2 = next.x + sign * STRAIGHT_ANGLE
                    val lat = (next.y - prev.y) * (if (x2 == x1) 1.0 / 2.0 else x1 / (x1 - x2)) + prev.y

                    currentPath.add(DoubleVector(sign * STRAIGHT_ANGLE, lat))
                    pathList.add(currentPath)
                    currentPath = ArrayList<DoubleVector>()
                    currentPath.add(DoubleVector(-sign * STRAIGHT_ANGLE, lat))
                }

                currentPath.add(next)
            }
        }

        pathList.add(currentPath)
        return pathList
    }

    fun createArcPath(path: List<DoubleVector>): List<DoubleVector> {
        val arcPath = ArrayList<DoubleVector>()
        if (path.isEmpty()) {
            return arcPath
        }

        arcPath.add(path[0])
        for (i in 1 until path.size) {
            addArcPointsToPath(arcPath, path[i - 1], path[i])
            arcPath.add(path[i])
        }

        return arcPath
    }

    private fun addArcPointsToPath(path: MutableList<DoubleVector>, start: DoubleVector, finish: DoubleVector) {
        val lonDelta = abs(start.x - finish.x)
        if (lonDelta <= LONGITUDE_EPS) {
            //the shortest path along a meridian
            return
        }

        if (abs(STRAIGHT_ANGLE - lonDelta) < LONGITUDE_EPS) {
            //the shortest path through North/South pole
            val latitude = (if (start.y + finish.y >= 0) +1 else -1) * STRAIGHT_ANGLE / 2
            path.add(DoubleVector(start.x, latitude))
            path.add(DoubleVector(finish.x, latitude))
            return
        }

        //calculate longitude direction
        val directionSign = (if (calculateIncreasingDistance(start.x, finish.x) <= calculateIncreasingDistance(
                finish.x,
                start.x
            )
        ) +1 else -1).toDouble()

        //calculate auxiliary constants
        val startLatTan = tan(degreeToRad(start.y))
        val finishLatTan = tan(degreeToRad(finish.y))
        val deltaLonSin = sin(degreeToRad(finish.x - start.x))

        //calculate path
        var longitude = start.x
        while (abs(longitude - finish.x) > LONGITUDE_EPS) {
            longitude += directionSign * LONGITUDE_EPS
            longitude = normalizeLon(longitude)

            val latitude = radToDegree(
                atan(
                    (finishLatTan * sin(degreeToRad(longitude - start.x)) + startLatTan * sin(
                        degreeToRad(
                            finish.x - longitude
                        )
                    )) / deltaLonSin
                )
            )

            path.add(DoubleVector(longitude, latitude))
        }
    }

    private fun calculateIncreasingDistance(start: Double, finish: Double): Double {
        val dist = finish - start
        return dist + if (dist < 0.0) FULL_ANGLE else 0.0
    }

    private fun degreeToRad(degree: Double): Double {
        return PI * degree / STRAIGHT_ANGLE
    }

    private fun radToDegree(rad: Double): Double {
        return STRAIGHT_ANGLE * rad / PI
    }

    internal fun calculateMaxZoom(rectSize: DoubleVector, containerSize: DoubleVector): Int {
        val xZoom = calculateMaxZoom(rectSize.x, containerSize.x)
        val yZoom = calculateMaxZoom(rectSize.y, containerSize.y)
        val zoom = min(xZoom, yZoom)
        return max(MIN_ZOOM, min(zoom, MAX_ZOOM))
    }

    private fun calculateMaxZoom(regionLength: Double, containerLength: Double): Int {
        if (regionLength == 0.0) {
            return MAX_ZOOM
        }
        return if (containerLength == 0.0) {
            MIN_ZOOM
        } else (ln(containerLength / regionLength) / ln(2.0)).toInt()
    }

    fun rescaleLengthByZoom(srcLength: Double, srcZoom: Int, dstZoom: Int): Double {
        return if (dstZoom >= srcZoom) {
            srcLength * (1 shl dstZoom - srcZoom)
        } else {
            srcLength / (1 shl srcZoom - dstZoom)
        }
    }

    internal fun <TypeT> calculateExtendedRectangleWithCenter(
        mapRuler: MapRuler<TypeT>,
        rect: Rect<TypeT>,
        center: Vec<TypeT>
    ): Rect<TypeT> {
        val radiusX = calculateRadius(
            center.x,
            rect.left,
            rect.width,
            mapRuler::distanceX)
        val radiusY = calculateRadius(
            center.y,
            rect.top,
            rect.height,
            mapRuler::distanceY)

        return Rect<TypeT>(
            center.x - radiusX,
            center.y - radiusY,
            radiusX * 2,
            radiusY * 2
        )
    }

    private fun calculateRadius(
        center: Double,
        left: Double,
        width: Double,
        distance: (Double, Double) -> Double
    ): Double {
        val right = left + width
        val minEdgeDistance = min(
            distance(center, left),
            distance(center, right)
        )
        return when (center) {
            in left..right -> width - minEdgeDistance
            else -> width + minEdgeDistance
        }
    }

    internal class LiveMapMappedValues {
        private val mappingMap = HashMap<String, String>()

        fun append(label: String, value: String) {
            mappingMap[label] = value
        }

        fun data(): List<String> {
            val mappingsList = ArrayList<String>(mappingMap.size * 2)

            mappingMap.forEach {
                mappingsList.add(it.key)
                mappingsList.add(it.value)
            }

            return mappingsList
        }
    }
}