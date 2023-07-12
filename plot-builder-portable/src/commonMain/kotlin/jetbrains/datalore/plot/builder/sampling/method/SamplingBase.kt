/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import jetbrains.datalore.plot.builder.sampling.Sampling

internal abstract class SamplingBase(val sampleSize: Int) : Sampling {

    init {
        check(this.sampleSize > 0) { "Sample size must be greater than zero, but was: " + this.sampleSize }
    }

    open fun isApplicable(population: DataFrame): Boolean {
        return population.rowCount() > sampleSize
    }
}
