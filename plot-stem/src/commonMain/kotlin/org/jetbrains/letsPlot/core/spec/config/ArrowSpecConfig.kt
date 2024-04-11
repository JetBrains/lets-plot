/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.spec.Option

internal class ArrowSpecConfig private constructor(options: Map<String, Any>) : OptionsAccessor(options) {

    fun createArrowSpec(): ArrowSpec {
        // See R function arrow(): https://www.rdocumentation.org/packages/grid/versions/3.4.1/topics/arrow
        val angle = getDouble(Option.Arrow.ANGLE) ?: DEF_ANGLE
        val length = getDouble(Option.Arrow.LENGTH) ?: DEF_LENGTH
        val minTailLength = getDouble(Option.Arrow.MIN_TAIL_LENGTH) ?: DEF_MIN_TAIL_LENGTH

        val end = getString(Option.Arrow.ENDS)?.let {
            when (it) {
                "last" -> ArrowSpec.End.LAST
                "first" -> ArrowSpec.End.FIRST
                "both" -> ArrowSpec.End.BOTH
                else -> throw IllegalArgumentException("Expected: first|last|both")
            }
        } ?: DEF_END

        val type = getString(Option.Arrow.TYPE)?.let {
            when (it) {
                "open" -> ArrowSpec.Type.OPEN
                "closed" -> ArrowSpec.Type.CLOSED
                else -> throw IllegalArgumentException("Expected: open|closed")
            }
        } ?: DEF_TYPE

        return ArrowSpec(toRadians(angle), length, end, type, minTailLength)
    }

    companion object {
        private const val DEF_ANGLE = 30.0
        private const val DEF_LENGTH = 10.0
        private val DEF_END = ArrowSpec.End.LAST
        private val DEF_TYPE = ArrowSpec.Type.OPEN
        private const val DEF_MIN_TAIL_LENGTH = 10.0

        fun create(options: Any): ArrowSpecConfig {
            if (options is Map<*, *>) {
                val name = ConfigUtil.featureName(options)
                if ("arrow" == name) {
                    @Suppress("UNCHECKED_CAST")
                    return ArrowSpecConfig(options as Map<String, Any>)
                }
            }
            throw IllegalArgumentException("Expected: 'arrow = arrow(...)'")
        }
    }
}
