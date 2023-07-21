package jetbrains.datalore.maps

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.gis.geoprotocol.Boundary
import jetbrains.livemap.mapengine.viewport.CellKey
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors.toList

object Utils {
    fun <T> any(): Boundary<T> {
        return square<T>(0.0, 0.0, 10.0, 10.0)
    }

    fun <T> empty(): Boundary<T> {
        return Boundary.create(MultiPolygon(emptyList()))
    }

    fun <T> square(left: Number, top: Number, width: Number, height: Number): Boundary<T> {
        val leftD = left.toDouble()
        val topD = top.toDouble()
        val widthD = width.toDouble()
        val heightD = height.toDouble()
        return Boundary.create(
            MultiPolygon(
                Polygon(
                    Ring(
                        listOf(
                            explicitVec(leftD, topD),
                            explicitVec(leftD + widthD, topD),
                            explicitVec(leftD + widthD, topD + heightD),
                            explicitVec(leftD, topD + heightD),
                            explicitVec(leftD, topD)
                        )
                    )
                )
            )
        )
    }

    fun p(x: Double, y: Double): DoubleVector {
        return DoubleVector(x, y)
    }

    fun <T> quads(vararg keys: String): Iterable<QuadKey<T>> {
        return Arrays.stream(keys).map(Function<String, QuadKey<T>>(::QuadKey)).collect(toList())
    }

    fun <T> quad(s: String): QuadKey<T> {
        return QuadKey(s)
    }

    fun cell(key: String): CellKey {
        return CellKey(key)
    }

    fun cells(vararg cells: CellKey): Collection<CellKey> {
        val list: MutableList<CellKey> = ArrayList<CellKey>()
        for (cell in cells) {
            list.add(cell)
        }
        return list
    }

    fun cells(vararg cells: String): Set<CellKey> {
        val set: MutableSet<CellKey> = HashSet<CellKey>()
        for (cell in cells) {
            set.add(CellKey(cell))
        }
        return set
    }
}