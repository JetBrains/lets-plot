/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform

open class MapperProviderAdapter<T> : MapperProvider<T> {
    override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<T> {
        throw IllegalStateException("Can't create mapper for discrete domain: ${domainValues.map { "'$it'" }
            .joinToString(limit = 3)}")
    }

    override fun createContinuousMapper(
        data: DataFrame,
        variable: DataFrame.Variable,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): GuideMapper<T> {
        throw IllegalStateException("Can't create mapper for continuous domain '$variable'")
    }
}
