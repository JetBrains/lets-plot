/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.builder.scale.provider.MapperProviderBase

abstract class DiscreteOnlyMapperProvider<T>(naValue: T) : MapperProviderBase<T>(naValue) {
    override fun createContinuousMapper(
        domain: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: ContinuousTransform
    ): GuideMapper<T> {
        throw IllegalStateException("[${this::class.simpleName}] Can't create mapper for continuous domain $domain")
    }
}
