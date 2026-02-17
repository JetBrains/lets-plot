/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.geom.BlankGeom.Companion.LabelX
import org.jetbrains.letsPlot.core.plot.base.geom.BlankGeom.Companion.LabelY
import org.jetbrains.letsPlot.core.plot.base.stat.Stats.R2
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.EqDataFrameField
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.annotation.SmoothAnnotationSpecification
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.AnnotationSpec.ANNOTATION_SIZE
import org.jetbrains.letsPlot.core.spec.Option.AnnotationSpec.USE_LAYER_COLOR
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.EQ
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.LABEL_X
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.LABEL_Y

class SmoothAnnotationConfig(
    opts: Map<String, Any>,
    varBindings: List<VarBinding>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarNames: List<String>?
): LineSpecConfig(opts, constantsMap, groupingVarNames, varBindings) {
    override val sourceRePattern = SOURCE_RE_PATTERN

    fun createAnnotations(): SmoothAnnotationSpecification {
        return create().run {
            val smoothOption = getMap(Option.LinesSpec.OPTIONS)

            SmoothAnnotationSpecification(
                valueSources = valueSources,
                linePatterns = linePatterns ?: emptyList(),
                textSize = getDouble(ANNOTATION_SIZE),
                useLayerColor = getBoolean(USE_LAYER_COLOR, false),
                labelX = labelPositionList(smoothOption[LABEL_X]) { labelPosition(it, ::positionX, LabelX.LEFT) },
                labelY = labelPositionList(smoothOption[LABEL_Y]) { labelPosition(it, ::positionY, LabelY.TOP) }
            )
        }
    }

    override fun createValueSource(fieldName: String, isAes: Boolean, format: String?): ValueSource {
        val eqSpec = getMap(Option.LinesSpec.OPTIONS)
            .let { options ->
                val eq = options[EQ] ?: emptyMap<String, Any>()
                require(eq is Map<*, *>) { "Not a Map: " + EQ + ": " + eq::class.simpleName }

                @Suppress("UNCHECKED_CAST")
                EqSpecConfig(eq as Map<String, Any>).create()
            }
        
        if (fieldName == EQ_PATTERN) {
            return EqDataFrameField(fieldName, format, eqSpec)
        }

        return super.createValueSource(fieldName, isAes, format)
    }

    override fun getValueSource(fieldString: String): ValueSource {
        if (fieldString == EQ_PATTERN) {
            return getValueSource(eqField())
        }

        return super.getValueSource(fieldString)
    }

    override fun prepareVariables(variables: List<String>): List<LinePattern> {
        if (variables.isEmpty() && lines == null) {
            val valueSource = getValueSource(varField(R2.name))
            return listOf(LinePattern.defaultLineForSmoothLabels(valueSource))
        }

        return super.prepareVariables(variables)
    }

    private fun eqField() = Field(EQ_PATTERN, false)

    companion object {
        private const val EQ_PATTERN = "~eq"
        @Suppress("RegExpRedundantEscape")
        private val SOURCE_RE_PATTERN = Regex("""(?:\\\^|\\@)|~eq|(\^\w+)|@(([\w^@]+)|(\{([\s\S]*?)\})|\.{2}\w+\.{2})""")

        private fun <T> labelPositionList(v: Any?, mapper: (Any?) -> T): List<T> =
            when (v) {
                null -> emptyList()
                is List<*> -> v.map(mapper)
                else -> listOf(mapper(v))
            }

        private fun <T> labelPosition(
            v: Any?,
            parsePos: (String) -> T,
            defaultPos: T
        ): Pair<Double?, T> =
            when (v) {
                is String -> null to parsePos(v)
                is Number -> v.toDouble() to defaultPos
                else -> null to defaultPos
            }

        fun positionX(x: String): LabelX {
            return when (x) {
                "left" -> LabelX.LEFT
                "center" -> LabelX.CENTER
                "right" -> LabelX.RIGHT
                else -> LabelX.LEFT
            }
        }

        fun positionY(y: String): LabelY {
            return when (y) {
                "top" -> LabelY.TOP
                "middle" -> LabelY.MIDDLE
                "bottom" -> LabelY.BOTTOM
                else -> LabelY.TOP
            }
        }
    }
}