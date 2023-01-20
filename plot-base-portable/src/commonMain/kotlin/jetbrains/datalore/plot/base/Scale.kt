/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface Scale {
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

    fun with(): Builder

    interface Builder {
        fun name(v: String): Builder

        fun breaks(l: List<Any>): Builder

        fun labels(l: List<String>): Builder

        fun labelFormatter(v: (Any) -> String): Builder

        fun multiplicativeExpand(v: Double): Builder

        fun additiveExpand(v: Double): Builder

        fun continuousTransform(v: ContinuousTransform): Builder

        fun breaksGenerator(v: BreaksGenerator): Builder

        fun build(): Scale
    }
}
