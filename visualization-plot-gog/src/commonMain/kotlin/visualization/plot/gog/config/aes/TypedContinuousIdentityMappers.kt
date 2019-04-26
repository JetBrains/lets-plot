package jetbrains.datalore.visualization.plot.gog.config.aes

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.Mappers
import kotlin.math.abs

object TypedContinuousIdentityMappers {
    val COLOR = { n: Double ->
        val `val` = abs(n.toInt())
        Color(
                `val` shr 16 and 0xff,
                `val` shr 8 and 0xff,
                `val` and 0xff
        )
    }

    private val MAP = HashMap<Aes<*>, (Double) -> Any?>()

    init {
        for (aes in Aes.numeric(Aes.values())) {
            MAP[aes] = Mappers.IDENTITY
        }

        MAP[Aes.COLOR] = COLOR
        MAP[Aes.FILL] = COLOR
    }

    fun contain(aes: Aes<*>): Boolean {
        return MAP.containsKey(aes)
    }

    operator fun <T> get(aes: Aes<T>): (Double) -> T {
        checkArgument(contain(aes), "No continuous identity mapper for aes " + aes.name())
        val f = MAP[aes]!!
        // Safe cast because this MAP has been filled cleanly, without 'Unchecked cast'-s
        return f as ((Double) -> T)
    }
}
