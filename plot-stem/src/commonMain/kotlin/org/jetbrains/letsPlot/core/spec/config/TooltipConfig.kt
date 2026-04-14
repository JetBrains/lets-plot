/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.StatKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehavior
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.spec.Option

class TooltipConfig(
    private val geomKind: GeomKind,
    private val statKind: StatKind,
    opts: Map<String, Any>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarNames: List<String>?,
    varBindings: List<VarBinding>
) : LineSpecConfig(opts, constantsMap, groupingVarNames, varBindings) {

    val anchor: TooltipAnchor? = readAnchor()
    val isCrosshairEnabled = isCrosshairEnabled(geomKind, anchor)
    val minWidth = getDouble(Option.Layer.Tooltips.TOOLTIP_MIN_WIDTH)
    val disableSplitting = getBoolean(Option.Layer.Tooltips.DISABLE_SPLITTING, def = false)
    val tooltipGroup = getString(Option.Layer.Tooltips.TOOLTIP_GROUP)

    //val lookupSpec: LookupSpec = readLookupSpec()

    //private fun readLookupSpec(): LookupSpec {
    //    val lookupSpace = when (getString(Option.Layer.Tooltips.LOOKUP_SPACE)) {
    //        Option.Layer.Tooltips.LookupSpace.X -> LookupSpace.X
    //        Option.Layer.Tooltips.LookupSpace.Y -> LookupSpace.Y
    //        Option.Layer.Tooltips.LookupSpace.XY -> LookupSpace.XY
    //        else -> defaultLookupSpace()
    //    }
    //
    //    val lookupStrategy = when (getString(Option.Layer.Tooltips.LOOKUP_STRATEGY)) {
    //        Option.Layer.Tooltips.LookupStrategy.NEAREST -> LookupStrategy.NEAREST
    //        Option.Layer.Tooltips.LookupStrategy.HOVER -> LookupStrategy.HOVER
    //        else -> defaultLookupStrategy()
    //    }
    //}

    fun createTooltips(): TooltipBehavior {
        val contentSpecification = create()
        val tooltipBehavior = TooltipBehavior(
            valueSources = contentSpecification.valueSources,
            tooltipLinePatterns = contentSpecification.linePatterns,
            tooltipTitle = contentSpecification.titleLine,

            anchor = anchor,
            minWidth = minWidth,
            disableSplitting = disableSplitting,
            tooltipGroup = tooltipGroup,
            isCrosshairEnabled = isCrosshairEnabled,
        )

        return createTooltipBehavior(geomKind = geomKind, statKind = statKind, tooltipBehavior)
    }

    private fun readAnchor(): TooltipAnchor? {
        if (!has(Option.Layer.Tooltips.TOOLTIP_ANCHOR)) {
            return null
        }

        return when (val anchor = getString(Option.Layer.Tooltips.TOOLTIP_ANCHOR)) {
            "top_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.LEFT)
            "top_center" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.CENTER)
            "top_right" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.RIGHT)
            "middle_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.MIDDLE, TooltipAnchor.HorizontalAnchor.LEFT)
            "middle_center" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.MIDDLE,
                TooltipAnchor.HorizontalAnchor.CENTER
            )

            "middle_right" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.MIDDLE,
                TooltipAnchor.HorizontalAnchor.RIGHT
            )

            "bottom_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.BOTTOM, TooltipAnchor.HorizontalAnchor.LEFT)
            "bottom_center" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.BOTTOM,
                TooltipAnchor.HorizontalAnchor.CENTER
            )

            "bottom_right" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.BOTTOM,
                TooltipAnchor.HorizontalAnchor.RIGHT
            )

            else -> throw IllegalArgumentException(
                "Illegal value $anchor, ${Option.Layer.Tooltips.TOOLTIP_ANCHOR}, expected values are: " +
                        "'top_left'/'top_center'/'top_right'/" +
                        "'middle_left'/'middle_center'/'middle_right'/" +
                        "'bottom_left'/'bottom_center'/'bottom_right'"
            )
        }
    }

    companion object {
        fun defaultTooltip(
            geomKind: GeomKind,
            statKind: StatKind,
        ): TooltipBehavior {
            return createTooltipBehavior(geomKind, statKind, TooltipBehavior.defaultTooltip())
        }

        private fun createTooltipBehavior(
            geomKind: GeomKind,
            statKind: StatKind,
            tooltipBehavior: TooltipBehavior,
        ): TooltipBehavior {
            val defaultTooltipBehavior = when {
                statKind == StatKind.SMOOTH && geomKind in listOf(GeomKind.POINT, GeomKind.CONTOUR) -> {
                    xUnivariateFunction(
                        lookupStrategy = LookupStrategy.NEAREST,
                        tooltipBehavior = tooltipBehavior
                    )
                }

                geomKind == GeomKind.RIBBON -> {
                    xUnivariateFunction(
                        lookupStrategy = LookupStrategy.NEAREST,
                        axisTooltipEnabledOverride = true,
                        tooltipBehavior = tooltipBehavior
                    )
                }

                geomKind in listOf(
                    GeomKind.DENSITY,
                    GeomKind.FREQPOLY,
                    GeomKind.HISTOGRAM,
                    GeomKind.DOT_PLOT,
                    GeomKind.LINE,
                    GeomKind.AREA,
                    GeomKind.BAR,
                    GeomKind.SEGMENT,
                    GeomKind.STEP,
                    GeomKind.POINT_RANGE,
                    GeomKind.LINE_RANGE,
                    GeomKind.ERROR_BAR
                ) -> {
                    xUnivariateFunction(
                        lookupStrategy = LookupStrategy.HOVER,
                        axisTooltipEnabledOverride = true,
                        tooltipBehavior = tooltipBehavior
                    )
                }

                geomKind == GeomKind.SMOOTH -> {
                    xUnivariateFunction(
                        //lookupStrategy = if (isCrosshairEnabled) LookupStrategy.NEAREST else LookupStrategy.HOVER,
                        lookupStrategy = LookupStrategy.HOVER,
                        tooltipBehavior = tooltipBehavior
                    )
                }

                geomKind in listOf(
                    GeomKind.PIE,
                    GeomKind.BOX_PLOT,
                    GeomKind.CROSS_BAR,
                    GeomKind.Y_DOT_PLOT,
                    GeomKind.HEX,
                    GeomKind.BIN_2D,
                    GeomKind.TILE
                ) -> {
                    bivariateFunction(
                        area = true,
                        axisTooltipEnabledOverride = true,
                        tooltipBehavior = tooltipBehavior
                    )
                }

                geomKind in listOf(
                    GeomKind.TEXT,
                    GeomKind.LABEL,
                    GeomKind.TEXT_REPEL,
                    GeomKind.LABEL_REPEL,
                    GeomKind.POINT,
                    GeomKind.JITTER,
                    GeomKind.Q_Q,
                    GeomKind.Q_Q_2,
                    GeomKind.CONTOUR,
                    GeomKind.DENSITY2D,
                    GeomKind.POINT_DENSITY,
                    GeomKind.AREA_RIDGES,
                    GeomKind.VIOLIN,
                    GeomKind.SINA,
                    GeomKind.LOLLIPOP,
                    GeomKind.SPOKE,
                    GeomKind.CURVE
                ) -> {
                    bivariateFunction(area = false, tooltipBehavior)
                }

                geomKind in listOf(GeomKind.Q_Q_LINE, GeomKind.Q_Q_2_LINE, GeomKind.PATH) -> {
                    bivariateFunction(
                        area = statKind !in listOf(StatKind.CONTOUR, StatKind.CONTOURF, StatKind.DENSITY2D),
                        tooltipBehavior = tooltipBehavior
                    )
                }

                geomKind in listOf(
                    GeomKind.H_LINE,
                    GeomKind.V_LINE,
                    GeomKind.BAND,
                    GeomKind.DENSITY2DF,
                    GeomKind.CONTOURF,
                    GeomKind.POLYGON,
                    GeomKind.MAP,
                    GeomKind.RECT
                ) -> bivariateFunction(area = true, tooltipBehavior)

                geomKind == GeomKind.LIVE_MAP -> bivariateFunction(area = false, tooltipBehavior)

                else -> noneTooltipBehavior(tooltipBehavior)
            }

            return defaultTooltipBehavior.withTooltipGroup(
                defaultTooltipBehavior.tooltipGroup ?: defaultTooltipGroup(geomKind)
            )
        }

        private fun xUnivariateFunction(
            lookupStrategy: LookupStrategy,
            axisTooltipEnabledOverride: Boolean? = null,
            tooltipBehavior: TooltipBehavior,
        ): TooltipBehavior {
            return tooltipBehavior.copy(
                lookupSpec = LookupSpec(LookupSpace.X, lookupStrategy),
                axisAesFromFunctionKind = listOf(Aes.X),
                axisTooltipEnabled = axisTooltipEnabledOverride ?: true,
                ignoreInvisibleTargets = false,
            )
        }

        private fun bivariateFunction(
            area: Boolean,
            tooltipBehavior: TooltipBehavior,
            axisTooltipEnabledOverride: Boolean? = null,
        ): TooltipBehavior {
            return tooltipBehavior.copy(
                lookupSpec = LookupSpec(
                    LookupSpace.XY,
                    if (area) LookupStrategy.HOVER else LookupStrategy.NEAREST
                ),
                axisAesFromFunctionKind = listOf(Aes.X, Aes.Y),
                axisTooltipEnabled = axisTooltipEnabledOverride ?: !area,
                ignoreInvisibleTargets = false,
            )
        }

        private fun noneTooltipBehavior(
            tooltipBehavior: TooltipBehavior
        ): TooltipBehavior {
            return tooltipBehavior.copy(
                lookupSpec = LookupSpec.NONE,
                axisAesFromFunctionKind = emptyList(),
                axisTooltipEnabled = true,
                ignoreInvisibleTargets = false,
            )
        }

        private fun defaultTooltipGroup(geomKind: GeomKind): String {
            return when (geomKind) {
                in LINE_LIKE_GEOMS -> AUTO_TOOLTIP_GROUP_LINE_LIKE
                else -> "$AUTO_TOOLTIP_GROUP_GEOM_PREFIX${geomKind.name.lowercase()}"
            }
        }

        private fun isCrosshairEnabled(geomKind: GeomKind, anchor: TooltipAnchor?): Boolean {
            if (anchor == null) {
                return false
            }

            return geomKind in setOf(
                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.SINA,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2,
                GeomKind.LINE,
                GeomKind.AREA,
                GeomKind.TILE,
                GeomKind.CONTOUR,
                GeomKind.CONTOURF,
                GeomKind.BIN_2D,
                GeomKind.HEX,
                GeomKind.DENSITY,
                GeomKind.DENSITY2D,
                GeomKind.DENSITY2DF,
                GeomKind.POINT_DENSITY,
                GeomKind.FREQPOLY,
                GeomKind.PATH,
                GeomKind.SEGMENT,
                GeomKind.CURVE,
                GeomKind.SPOKE,
                GeomKind.RIBBON,
                GeomKind.SMOOTH,
                GeomKind.STEP
            )
        }

        private val LINE_LIKE_GEOMS = setOf(
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.SMOOTH,
            GeomKind.STEP,
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.RIBBON,
            GeomKind.SEGMENT,
            GeomKind.SPOKE
        )

        private const val AUTO_TOOLTIP_GROUP_LINE_LIKE = "__auto_group_line_like__"
        private const val AUTO_TOOLTIP_GROUP_GEOM_PREFIX = "__auto_group_geom__:"
    }
}
