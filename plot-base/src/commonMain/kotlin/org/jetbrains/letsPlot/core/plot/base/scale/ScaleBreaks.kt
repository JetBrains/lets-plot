/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms

class ScaleBreaks private constructor(
    val domainValues: List<Any>,
    val transformedValues: List<Double>,
    val transform: Transform,
    val labels: List<String>,
    val fixed: Boolean,
    val formatter: (Any) -> String,
) {
    init {
        check(domainValues.size == transformedValues.size) {
            "Scale breaks size: ${domainValues.size} transformed size: ${transformedValues.size} but expected to be the same"
        }
        check(domainValues.size == transformedValues.size) {
            "Scale breaks size: ${domainValues.size} transformed size: ${transformedValues.size} but expected to be the same"
        }
    }

    val isEmpty: Boolean
        get() = domainValues.isEmpty()

    val size: Int
        get() = domainValues.size

    fun withOneBreak(): ScaleBreaks {
        check(!isEmpty) { "Can't get one break from an empty scale breaks." }
        return ScaleBreaks(
            this.domainValues.subList(0, 1),
            this.transformedValues.subList(0, 1),
            this.transform,
            this.labels.subList(0, 1),
            this.fixed,
            this.formatter
        )
    }

    fun withFixedBreaks(domainValues: List<Any>, labels: List<String>?): ScaleBreaks {
        @Suppress("NAME_SHADOWING")
        val labels = labels ?: domainValues.map(formatter)
        val (
            filteredDomainValues,
            filteredTransformedValues,
            filteredLabels
        ) = applyTransformAndDropNulls(
            transform,
            domainValues,
            labels,
        )
        return ScaleBreaks(
            domainValues = filteredDomainValues,
            transformedValues = filteredTransformedValues,
            transform = transform,
            labels = filteredLabels,
            fixed = true,
            formatter = formatter,
        )
    }

    fun withTransform(transform: ContinuousTransform): ScaleBreaks {
        val (
            filteredDomainValues,
            filteredTransformedValues,
            filteredLabels
        ) = applyTransformAndDropNulls(
            transform,
            domainValues,
            labels,
        )

        return ScaleBreaks(
            domainValues = filteredDomainValues,
            transformedValues = filteredTransformedValues,
            transform = transform,
            labels = filteredLabels,
            fixed = fixed,
            formatter = formatter,
        )
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
                transform = this.transform,
                labels = labels.slice(includeIndices),
                fixed = fixed,
                formatter = formatter,
            )
        }
    }


    companion object {
        val IDENTITY_FORMATTER: (Any) -> String = { v -> v.toString() }

        private val DUMMY_FORMATTER: (Any) -> String =
            { v -> throw IllegalStateException("An attempt to format $v using 'dummy formatter'.") }

        val EMPTY: ScaleBreaks = ScaleBreaks(
            emptyList(),
            emptyList(),
            Transforms.IDENTITY,
            emptyList(),
            true,
            DUMMY_FORMATTER
        )

        private fun applyTransformAndDropNulls(
            transform: Transform,
            domainValues: List<Any>,
            labels: List<String>
        ): Triple<List<Any>, List<Double>, List<String>> {
            val transformCore = transform.unwrap() // make sure 'original' transform is used (i.e. without user limits).
            val transformed = ScaleUtil.applyTransform(domainValues, transformCore)

            // drop NULLs which can occure after transform.
            val keepIndices: Set<Int> = transformed
                .mapIndexed { i, v -> if (v == null) null else i }
                .filterNotNull()
                .toSet()

            val filteredDomainValues = domainValues.filterIndexed { i, _ -> i in keepIndices }
            val filteredTransformedValues = transformed.filterNotNull()
            val filteredLabels = labels.filterIndexed { i, _ -> i in keepIndices }
            return Triple(
                filteredDomainValues,
                filteredTransformedValues,
                filteredLabels
            )
        }
    }

    object Fixed {
        fun withTransform(
            domainValues: List<Any>,
            transform: Transform,
            formatter: (Any) -> String,
            alternativeLabels: List<String>? = null,
        ): ScaleBreaks {
            @Suppress("DuplicatedCode")
            if (alternativeLabels != null) {
                check(domainValues.size == alternativeLabels.size) {
                    "Scale breaks size: ${domainValues.size} and labels size: ${alternativeLabels.size} but expected to be the same"
                }
            }
            val labels = alternativeLabels ?: domainValues.map(formatter)
            val (
                filteredDomainValues,
                filteredTransformedValues,
                filteredLabels
            ) = applyTransformAndDropNulls(
                transform,
                domainValues,
                labels,
            )
            return ScaleBreaks(
                domainValues = filteredDomainValues,
                transformedValues = filteredTransformedValues,
                transform = transform,
                labels = filteredLabels,
                fixed = true,
                formatter = formatter,
            )
        }
    }

    object ContinuousFlex {

        fun noTransform(
            domainValues: List<Double>,
            formatter: (Any) -> String,
            alternativeLabels: List<String>? = null,
        ): ScaleBreaks {
            return withTransform(
                domainValues,
                Transforms.IDENTITY,
                formatter,
                alternativeLabels,
            )
        }

        private fun withTransform(
            domainValues: List<Double>,
            transform: Transform,
            formatter: (Any) -> String,
            alternativeLabels: List<String>? = null,
        ): ScaleBreaks {
            if (alternativeLabels != null) {
                check(domainValues.size == alternativeLabels.size) {
                    "Scale breaks size: ${domainValues.size} and labels size: ${alternativeLabels.size} but expected to be the same"
                }
            }

            val labels = alternativeLabels ?: domainValues.map(formatter)
            val (
                filteredDomainValues,
                filteredTransformedValues,
                filteredLabels
            ) = applyTransformAndDropNulls(
                transform,
                domainValues,
                labels,
            )

            return ScaleBreaks(
                domainValues = filteredDomainValues,
                transformedValues = filteredTransformedValues,
                transform = transform,
                labels = filteredLabels,
                fixed = false,
                formatter = formatter,
            )
        }
    }

    object DemoAndTest {
        fun continuous(
            domainValues: List<Double>,
            formatter: ((Any) -> String)? = null
        ): ScaleBreaks {
            @Suppress("NAME_SHADOWING")
            val formatter = formatter ?: ScaleBreaks.IDENTITY_FORMATTER
            return ScaleBreaks(
                domainValues = domainValues,
                transformedValues = domainValues,
                transform = Transforms.IDENTITY,
                labels = domainValues.map(formatter),
                fixed = true,
                formatter = formatter,
            )
        }

        fun continuous(
            domainValues: List<Double>,
            labels: List<String>,
        ): ScaleBreaks {
            return ScaleBreaks(
                domainValues = domainValues,
                transformedValues = domainValues,
                transform = Transforms.IDENTITY,
                labels = labels,
                fixed = true,
                formatter = DUMMY_FORMATTER,
            )
        }
    }
}