/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.scale.provider.MapperProviderBase

abstract class ContinuousOnlyMapperProvider<T>(naValue: T) : MapperProviderBase<T>(naValue) {
    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<T> {
        throw IllegalStateException("[${this::class.simpleName}] Can't create mapper for discrete domain")
    }
}
