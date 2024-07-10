/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

class ScaleBreaks constructor(
    val domainValues: List<Any>,
    val transformedValues: List<Double>,
    val labels: List<String>,
    val fixed: Boolean,
    val formatter: (Any) -> String,
) {
    // For auto-generated breaks.
    constructor(
        domainValues: List<Any>,
        transformedValues: List<Double>,
        formatter: (Any) -> String,
    ) : this(
        domainValues,
        transformedValues,
        labels = domainValues.map { formatter(it) },
        fixed = false,
        formatter = formatter,
    )

    // For manual breaks or categoricals.
    constructor(
        domainValues: List<Any>,
        transformedValues: List<Double>,
        labels: List<String>,
    ) : this(
        domainValues,
        transformedValues,
        labels = labels,
        fixed = true,
        formatter = DUMMY_FORMATTER,
    )

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

    fun filterByTransformedLimits(limits: DoubleSpan): ScaleBreaks {
        val includeIndices = transformedValues.mapIndexed { index, value ->
            val include = value >= limits.lowerEnd && value <= limits.upperEnd
            if (include) index else null
        }.filterNotNull()

        return if (includeIndices.size == transformedValues.size) {
            this
        } else {
            ScaleBreaks(
                domainValues = domainValues.slice(includeIndices),
                transformedValues = transformedValues.slice(includeIndices),
                labels = labels.slice(includeIndices),
                fixed = fixed,
                formatter = formatter,
            )
        }
    }

    companion object {
        internal val DUMMY_FORMATTER: (Any) -> String =
            { v -> throw IllegalStateException("An attempt to format $v using 'dummy formatter'.") }

        val EMPTY: ScaleBreaks = ScaleBreaks(emptyList(), emptyList(), emptyList())
    }
}
