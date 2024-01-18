/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

class ScaleBreaks(
    val domainValues: List<Any>,
    val transformedValues: List<Double>,
    val labels: List<String>
) {
    val isEmpty: Boolean
        get() = domainValues.isEmpty()

    val size: Int
        get() = domainValues.size

    init {
        require(domainValues.size == transformedValues.size) {
            "Scale breaks size: ${domainValues.size} transformed size: ${transformedValues.size} but expected to be the same"
        }
        require(domainValues.size == labels.size) {
            "Scale breaks size: ${domainValues.size} labels size: ${labels.size} but expected to be the same"
        }
    }

    fun projectOnAxis(
        axisDomain: DoubleSpan,
        axisLength: Double,
        isHorizontal: Boolean
    ): List<Double> {
        // Do reverse maping for vertical axis: screen coordinates: top->bottom, but y-axis coordinate: bottom->top
        val reverse = !isHorizontal
        val mapper = Mappers.linear(
            domain = axisDomain,
            range = DoubleSpan(0.0, axisLength),
            reverse = reverse
        )
        return transformedValues.mapIndexed { i, v ->
            mapper(v)
                ?: throw IllegalStateException("Can't project axis break ${labels[i]} ($v) to axis (horiz:$isHorizontal)")
        }
    }


    companion object {
        val EMPTY: ScaleBreaks = ScaleBreaks(emptyList(), emptyList(), emptyList())
    }
}
