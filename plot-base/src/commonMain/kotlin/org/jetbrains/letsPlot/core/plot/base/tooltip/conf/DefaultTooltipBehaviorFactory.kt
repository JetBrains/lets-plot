/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.StatKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.*

object DefaultTooltipBehaviorFactory {
    fun create(
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

        return if (tooltipBehavior.isCrosshairEnabled) {
            defaultTooltipBehavior.copy(
                lookupSpec = defaultTooltipBehavior.lookupSpec.copy(lookupStrategy = LookupStrategy.NEAREST)
            )
        } else {
            defaultTooltipBehavior
        }
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
}
