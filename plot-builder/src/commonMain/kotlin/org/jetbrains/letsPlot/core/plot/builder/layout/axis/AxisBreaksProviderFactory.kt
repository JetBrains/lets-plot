/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator

abstract class AxisBreaksProviderFactory {
    abstract fun createAxisBreaksProvider(axisDomain: DoubleSpan): AxisBreaksProvider

    companion object {
        fun forScale(scale: Scale): AxisBreaksProviderFactory {
            return if (scale.hasBreaks()) {
                FixedBreaksProviderFactory(FixedAxisBreaksProvider(scale.getScaleBreaks()))
            } else {
                AdaptableBreaksProviderFactory(scale.getBreaksGenerator())
            }
        }
    }

    class FixedBreaksProviderFactory(private val breaksProvider: FixedAxisBreaksProvider) :
        AxisBreaksProviderFactory() {
        override fun createAxisBreaksProvider(axisDomain: DoubleSpan): AxisBreaksProvider {
            return breaksProvider
        }
    }

    class AdaptableBreaksProviderFactory(private val breaksGenerator: BreaksGenerator) : AxisBreaksProviderFactory() {
        override fun createAxisBreaksProvider(axisDomain: DoubleSpan): AxisBreaksProvider {
            return AdaptableAxisBreaksProvider(axisDomain, breaksGenerator)
        }
    }
}