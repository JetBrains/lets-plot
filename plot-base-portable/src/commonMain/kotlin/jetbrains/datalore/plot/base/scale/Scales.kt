/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.Scale

object Scales {

    fun continuousDomain(
        name: String,
        continuousRange: Boolean
    ): Scale {
        return ContinuousScale(name, continuousRange)
    }

    fun discreteDomain(
        name: String,
        discreteTransform: DiscreteTransform,
    ): Scale {
        return DiscreteScale(name, discreteTransform)
    }

    /**
     * Functions to be used in demos and tests only.
     */
    object DemoAndTest {
        fun discreteDomain(
            name: String,
            domainValues: List<Any>,
            domainLimits: List<Any> = emptyList(),
        ): Scale {
            return DiscreteScale(
                name,
                DiscreteTransform(domainValues, domainLimits),
            )
        }

        fun pureDiscrete(
            name: String,
            domainValues: List<Any>,
        ): Scale {
            val transform = DiscreteTransform(domainValues, emptyList())
            return DiscreteScale(name, transform)
        }

        fun continuousDomain(name: String, aes: Aes<*>): Scale {
            return ContinuousScale(
                name,
                aes.isNumeric
            )
        }

        fun continuousDomainNumericRange(name: String): Scale {
            return ContinuousScale(
                name,
                true
            )
        }
    }
}
