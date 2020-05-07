/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.sampling.method

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.sampling.Sampling

internal abstract class SamplingBase(val sampleSize: Int) :
    Sampling {

    init {
        checkState(this.sampleSize > 0, "Sample size must be greater than zero, but was: " + this.sampleSize)
    }

    open fun isApplicable(population: DataFrame): Boolean {
        return population.rowCount() > sampleSize
    }
}
