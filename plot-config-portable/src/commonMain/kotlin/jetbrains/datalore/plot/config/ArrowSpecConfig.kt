/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec

internal class ArrowSpecConfig private constructor(options: Map<String, Any>) : OptionsAccessor(options) {

    fun createArrowSpec(): ArrowSpec {
        // See R function arrow(): https://www.rdocumentation.org/packages/grid/versions/3.4.1/topics/arrow
        var angle = DEF_ANGLE
        var length = DEF_LENGTH
        var end = DEF_END
        var type = DEF_TYPE

        if (has(Option.Arrow.ANGLE)) {
            angle = getDouble(Option.Arrow.ANGLE)!!
        }
        if (has(Option.Arrow.LENGTH)) {
            length = getDouble(Option.Arrow.LENGTH)!!
        }
        if (has(Option.Arrow.ENDS)) {
            val s = getString(Option.Arrow.ENDS)
            when (s) {
                "last" -> end = ArrowSpec.End.LAST
                "first" -> end = ArrowSpec.End.FIRST
                "both" -> end = ArrowSpec.End.BOTH
                else -> throw IllegalArgumentException("Expected: first|last|both")
            }
        }
        if (has(Option.Arrow.TYPE)) {
            val s = getString(Option.Arrow.TYPE)
            when (s) {
                "open" -> type = ArrowSpec.Type.OPEN
                "closed" -> type = ArrowSpec.Type.CLOSED
                else -> throw IllegalArgumentException("Expected: open|closed")
            }
        }

        return ArrowSpec(toRadians(angle), length, end, type)
    }

    companion object {
        private const val DEF_ANGLE = 30.0
        private const val DEF_LENGTH = 10.0
        private val DEF_END = ArrowSpec.End.LAST
        private val DEF_TYPE = ArrowSpec.Type.OPEN

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
