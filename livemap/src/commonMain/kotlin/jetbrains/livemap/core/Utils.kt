package jetbrains.livemap.core

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Rectangle

object Utils {
    fun toRectangle(r: DoubleRectangle): Rectangle {
        return Rectangle(
            r.origin.x.toInt(),
            r.origin.y.toInt(),
            r.dimension.x.toInt(),
            r.dimension.y.toInt()
        )
    }

    fun formatDouble(v: Double, precision: Int): String {
        val updateMs = v.toInt()
        val div = ((v - updateMs.toDouble()) * 10.0 * precision.toDouble()).toInt()
        return "$updateMs.$div"
    }

    // a without b
    fun <T> diff(a: Set<T>, b: Set<T>): Set<T> {
        val result = HashSet(a)
        result.removeAll(b)

        return result
    }

    fun <T> common(a: Set<T>, b: Set<T>): Set<T> {
        if (a.isEmpty() || b.isEmpty()) {
            return emptySet()
        }

        val res = HashSet(a)
        res.removeAll { t -> !b.contains(t) }

        return res
    }

    fun <KeyT, ValueT> provide(key: KeyT, map: MutableMap<KeyT, ValueT>, def: () -> ValueT): ValueT? {
        if (!map.containsKey(key)) {
            map[key] = def()
        }

        return map[key]
    }
}