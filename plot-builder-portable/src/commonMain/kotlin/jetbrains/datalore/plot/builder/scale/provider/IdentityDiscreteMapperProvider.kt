/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

open class IdentityDiscreteMapperProvider<T>(
    private val inputConverter: (Any?) -> T?, naValue: T
) : MapperProviderBase<T>(naValue) {

    override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<T> {
        val outputValues = ArrayList<T>()
        for (inputValue in domainValues) {
            if (inputValue == null) {
                outputValues.add(naValue)
            } else {
                val outputValue = inputConverter(inputValue)
                    ?: throw IllegalStateException("Can't map input value $inputValue to output type")
                outputValues.add(outputValue)
            }
        }
        return GuideMappers.discreteToDiscrete(domainValues, outputValues, naValue)
    }
}
