/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks

/**
 * Translates input to aesthetics by
 * 1) Transforming data (like x1=log(x)). Must happen before 'stat' is applies to data
 * 2) Mapping data to aesthetic
 *
 *
 * name - (axis/legend title)
 * breaks (domain values) - ticks on axis, items/segments on legends
 * labels - tick labels
 *
 * @param <T> - type of target aesthetic
 *
 */
interface Scale<T> {   // ToDo: remove <T>: it only make sense for mapper.
    val name: String

    val labelFormatter: ((Any) -> String)?

    /**
     * @return TRUE if both, domain and range are continuous
     */
    val isContinuous: Boolean

    val isContinuousDomain: Boolean

    val multiplicativeExpand: Double

    val additiveExpand: Double

    val transform: Transform

    fun hasBreaks(): Boolean

    fun getBreaksGenerator(): BreaksGenerator

    fun getScaleBreaks(): ScaleBreaks

    fun with(): Builder<T>

    interface Builder<T> {

        fun breaks(l: List<Any>): Builder<T>

        fun labels(l: List<String>): Builder<T>

        fun labelFormatter(v: (Any) -> String): Builder<T>

        fun multiplicativeExpand(v: Double): Builder<T>

        fun additiveExpand(v: Double): Builder<T>

        fun continuousTransform(v: ContinuousTransform): Builder<T>

        fun breaksGenerator(v: BreaksGenerator): Builder<T>

        fun build(): Scale<T>
    }
}
