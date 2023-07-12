/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

interface StatContext {
    fun overallXRange(): DoubleSpan?

    fun overallYRange(): DoubleSpan?

    fun mappedStatVariables(): List<DataFrame.Variable>

    fun getFlipped(): StatContext {
        return Flipped(this)
    }

    class Flipped(private val orig: StatContext) : StatContext {
        override fun overallXRange(): DoubleSpan? {
            return orig.overallYRange()
        }

        override fun overallYRange(): DoubleSpan? {
            return orig.overallXRange()
        }

        override fun mappedStatVariables(): List<DataFrame.Variable> {
            return orig.mappedStatVariables()
        }

        override fun getFlipped(): StatContext {
            return orig
        }
    }
}
