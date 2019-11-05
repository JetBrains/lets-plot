/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform

interface MapperProvider<T> {
    /**
     * Create mapper with discrete input (domain)
     */
    // ToDo: add limits to this method
    fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<T>

    /**
     * Create mapper with continuous (numeric) input (domain)
     */
    fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<T>
}
