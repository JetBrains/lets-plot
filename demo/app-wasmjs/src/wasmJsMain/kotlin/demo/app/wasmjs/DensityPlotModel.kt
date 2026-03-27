/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.plotson.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

internal class DensityPlotModel(
    private val maxPoints: Int = 600,
    private val seed: Int = 12,
) {
    var distribution: Distribution = Distribution.NORMAL
    var useFixedLimits: Boolean = true
    val samples: List<Double> get() = mySamples

    private val mySamples = mutableListOf<Double>()
    private var random = Random(seed)

    fun step(n: Int = 1) {
        repeat(n) {
            mySamples.add(distribution.nextValue(random))
            if (mySamples.size > maxPoints) {
                mySamples.removeFirst()
            }
        }
    }

    fun reset() {
        mySamples.clear()
        random = Random(seed)
    }

    fun buildPlotSpec(): MutableMap<String, Any> {
        return plot {
            layerOptions += layer {
                geom = GeomKind.DENSITY
                data = mapOf("x" to mySamples)
                mapping = Mapping(Aes.X to "x")
                color = "black"
                size = 1.2
            }

            if (useFixedLimits) {
                scaleOptions += scale { aes = Aes.X; limits = listOf(-3.0, 3.0) }
                scaleOptions += scale { aes = Aes.Y; limits = listOf(0.0, 0.7) }
            }
        }.toJson()
    }
}

internal enum class Distribution(
    val id: String,
    val label: String,
    val nextValue: (Random) -> Double
) {
    NORMAL(
        id = "normal",
        label = "normal",
        nextValue = { random -> normal(random) }
    ),
    SHIFTED(
        id = "shifted",
        label = "shifted gaussian",
        nextValue = { random -> normal(random, mean = 1.2, stdDev = 0.65) }
    ),
    BIMODAL(
        id = "bimodal",
        label = "bimodal",
        nextValue = { random ->
            if (random.nextDouble() < 0.5) {
                normal(random, mean = -1.35, stdDev = 0.35)
            } else {
                normal(random, mean = 1.35, stdDev = 0.35)
            }
        }
    ),
    UNIFORM(
        id = "uniform",
        label = "uniform",
        nextValue = { random -> random.nextDouble(from = -2.5, until = 2.5) }
    ),
    LAPLACE(
        id = "laplace",
        label = "laplace",
        nextValue = { random -> laplace(random) }
    );

    companion object {
        fun fromId(id: String): Distribution {
            return entries.firstOrNull { it.id == id } ?: NORMAL
        }
    }
}

private fun normal(random: Random, mean: Double = 0.0, stdDev: Double = 1.0): Double {
    val u1 = random.nextDouble()
    val u2 = random.nextDouble()
    val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
    return mean + stdDev * z0
}

private fun laplace(random: Random, mean: Double = 0.0, scale: Double = 0.7): Double {
    val u = random.nextDouble() - 0.5
    return mean - scale * if (u < 0) ln(1 + 2 * u) else -ln(1 - 2 * u)
}
