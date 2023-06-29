/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.interval.DoubleSpan

interface StatContext {
    fun overallXRange(): DoubleSpan?

    fun overallYRange(): DoubleSpan?

    fun getMapping(): Map<Aes<*>, DataFrame.Variable> = throw IllegalStateException("Not implemented")

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

        override fun getMapping(): Map<Aes<*>, DataFrame.Variable> {
            return orig.getMapping().map { (aes, variable) ->
                when (aes) {
                    Aes.X -> Aes.Y to variable
                    Aes.Y -> Aes.X to variable
                    else -> aes to variable
                }
            }.toMap()
        }

        override fun getFlipped(): StatContext {
            return orig
        }
    }
}
