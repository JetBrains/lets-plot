/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option.Meta.KIND
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.SUBPLOTS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots
import org.jetbrains.letsPlot.core.spec.config.BunchConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.PlotSizeHelper

/**
 * Transforms old 'GGBanch' specs to new 'ggbanch' composite figure specs
 * January 15, 2025 : remove all after a reasonable period of time.
 */
internal object SpecGGBunchTransformBackendUtil {
    fun ggbunchFromGGBunch(bunchSpecOld: MutableMap<String, Any>): MutableMap<String, Any> {
        // Make sure it's a bunch (old)
        check(PlotConfig.figSpecKind(bunchSpecOld) == FigKind.GG_BUNCH_SPEC) {
            "Plot Bunch is expected but was kind: ${PlotConfig.figSpecKind(bunchSpecOld)}"
        }

        // estimate bunch size
        val wasBunchSize = PlotSizeHelper.plotBunchSize(bunchSpecOld)

        // transform to new format
        val wasBunchConfig = BunchConfig(bunchSpecOld)
        val wasItemBoundsList = wasBunchConfig.bunchItems.map { item ->
            val size = PlotSizeHelper.bunchItemSize(item)
            DoubleRectangle.XYWH(
                item.x,
                item.y,
                size.x,
                size.y
            )
        }

        val scalerX = 1.0 / (if (wasBunchSize.x > 0) wasBunchSize.x else 1.0)
        val scalerY = 1.0 / (if (wasBunchSize.y > 0) wasBunchSize.y else 1.0)
        val relativeItemBoundsList = wasItemBoundsList.map {
            listOf(
                it.left * scalerX,
                it.top * scalerY,
                it.width * scalerX,
                it.height * scalerY
            )
        }

        val figureSpecs: List<Map<String, Any>> = wasBunchConfig.bunchItems.map {
            it.featureSpec
        }

        val bunchSpecNew = mapOf(
            KIND to SUBPLOTS,
            SubPlots.FIGURES to figureSpecs,
            SubPlots.LAYOUT to mapOf(
                SubPlots.Layout.NAME to SubPlots.Layout.SUBPLOTS_FREE,
                SubPlots.Free.REGIONS to relativeItemBoundsList
            )
        )
        return HashMap(bunchSpecNew)
    }
}