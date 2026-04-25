/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.Companion.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.Companion.HorizontalPlacement
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.Companion.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.Companion.VerticalPlacement
import org.jetbrains.letsPlot.core.plot.base.stat.Stats.R2
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.EqDataFrameField
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.annotation.PositionedAnnotationSpecification
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.AnnotationSpec.ANNOTATION_SIZE
import org.jetbrains.letsPlot.core.spec.Option.AnnotationSpec.USE_LAYER_COLOR
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.EQ
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.LABEL_X
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.LABEL_Y

class SmoothStatSummaryAnnotationConfig(
    opts: Map<String, Any>,
    varBindings: List<VarBinding>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarNames: List<String>?
) : OptionsAccessor(opts) {
    private val lineSpecParser = LineSpecParser(
        opts = opts,
        constantsMap = constantsMap,
        groupingVarNames = groupingVarNames,
        varBindings = varBindings,
        customization = SmoothLineSpecCustomization
    )

    fun createAnnotations(): PositionedAnnotationSpecification {
        val contentSpecification = lineSpecParser.create()
        val smoothOption = getMap(Option.LinesSpec.OPTIONS)

        return PositionedAnnotationSpecification(
            valueSources = contentSpecification.valueSources,
            linePatterns = contentSpecification.linePatterns ?: emptyList(),
            textSize = getDouble(ANNOTATION_SIZE),
            useLayerColor = getBoolean(USE_LAYER_COLOR, false),
            horizontalPlacements = labelPositionList(smoothOption[LABEL_X], ::labelHorizontalPlacement),
            verticalPlacements = labelPositionList(smoothOption[LABEL_Y], ::labelVerticalPlacement)
        )
    }

    companion object {
        internal const val EQ_PATTERN = "~eq"

        @Suppress("RegExpRedundantEscape")
        internal val SOURCE_RE_PATTERN = Regex("""(?:\\\^|\\@)|~eq|(\^\w+)|@(([\w^@]+)|(\{([\s\S]*?)\})|\.{2}\w+\.{2})""")

        private fun <T> labelPositionList(v: Any?, mapper: (Any?) -> T): List<T> =
            when (v) {
                null -> emptyList()
                is List<*> -> v.map(mapper)
                else -> listOf(mapper(v))
            }

        private fun labelHorizontalPlacement(v: Any?): HorizontalPlacement =
            when (v) {
                is String -> HorizontalPlacement(null, horizontalAnchor(v))
                is Number -> HorizontalPlacement(v.toDouble(), HorizontalAnchor.LEFT)
                else -> HorizontalPlacement(null, HorizontalAnchor.LEFT)
            }

        private fun labelVerticalPlacement(v: Any?): VerticalPlacement =
            when (v) {
                is String -> VerticalPlacement(null, verticalAnchor(v))
                is Number -> VerticalPlacement(v.toDouble(), VerticalAnchor.TOP)
                else -> VerticalPlacement(null, VerticalAnchor.TOP)
            }

        fun horizontalAnchor(x: String): HorizontalAnchor {
            return when (x) {
                "left" -> HorizontalAnchor.LEFT
                "center" -> HorizontalAnchor.CENTER
                "right" -> HorizontalAnchor.RIGHT
                else -> HorizontalAnchor.LEFT
            }
        }

        fun verticalAnchor(y: String): VerticalAnchor {
            return when (y) {
                "top" -> VerticalAnchor.TOP
                "center" -> VerticalAnchor.CENTER
                "bottom" -> VerticalAnchor.BOTTOM
                else -> VerticalAnchor.TOP
            }
        }

        private object SmoothLineSpecCustomization : LineSpecParser.Customization {
            override val sourceRePattern: Regex = SOURCE_RE_PATTERN

            override fun createValueSource(
                parser: LineSpecParser,
                fieldName: String,
                isAes: Boolean,
                format: String?
            ): ValueSource {
                if (fieldName == EQ_PATTERN) {
                    val eqSpec = parser.getMap(Option.LinesSpec.OPTIONS)
                        .let { options ->
                            val eq = options[EQ] ?: emptyMap<String, Any>()
                            require(eq is Map<*, *>) { "Not a Map: " + EQ + ": " + eq::class.simpleName }

                            @Suppress("UNCHECKED_CAST")
                            EqSpecConfig(eq as Map<String, Any>).create()
                        }

                    return EqDataFrameField(fieldName, format, eqSpec)
                }

                return parser.createDefaultValueSource(fieldName, isAes, format)
            }

            override fun resolveField(
                parser: LineSpecParser,
                fieldString: String
            ): LineSpecParser.Field? {
                return if (fieldString == EQ_PATTERN) {
                    parser.varField(EQ_PATTERN)
                } else {
                    null
                }
            }

            override fun prepareVariables(
                parser: LineSpecParser,
                variables: List<String>
            ): List<LinePattern> {
                if (variables.isEmpty() && parser.lines == null) {
                    val valueSource = parser.getValueSource(parser.varField(R2.name))
                    return listOf(LinePattern.defaultLineForSmoothLabels(valueSource))
                }

                return parser.prepareDefaultVariables(variables)
            }
        }
    }
}
