/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.builder.assemble.ColorBarOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendOptions
import org.jetbrains.letsPlot.core.spec.Option.Guide.COLOR_BAR
import org.jetbrains.letsPlot.core.spec.Option.Guide.COLOR_BAR_GB
import org.jetbrains.letsPlot.core.spec.Option.Guide.ColorBar.BIN_COUNT
import org.jetbrains.letsPlot.core.spec.Option.Guide.ColorBar.HEIGHT
import org.jetbrains.letsPlot.core.spec.Option.Guide.ColorBar.WIDTH
import org.jetbrains.letsPlot.core.spec.Option.Guide.LEGEND
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.BY_ROW
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.COL_COUNT
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.ROW_COUNT
import org.jetbrains.letsPlot.core.spec.Option.Guide.NONE
import org.jetbrains.letsPlot.core.spec.Option.Guide.TITLE
import kotlin.math.max

abstract class GuideConfig private constructor(opts: Map<String, Any>) : OptionsAccessor(opts) {

    fun createGuideOptions(): GuideOptions {
        val options = createGuideOptionsIntern()
        return options
            .withTitle(getString(TITLE))
    }

    protected abstract fun createGuideOptionsIntern(): GuideOptions

    private class GuideNoneConfig internal constructor() : GuideConfig(emptyMap()) {

        override fun createGuideOptionsIntern(): GuideOptions {
            return GuideOptions.NONE
        }
    }

    private class LegendConfig internal constructor(opts: Map<String, Any>) : GuideConfig(opts) {

        override fun createGuideOptionsIntern(): GuideOptions {
            return LegendOptions(
                colCount = getDouble(COL_COUNT)?.toInt()?.let { max(1, it) },
                rowCount = getDouble(ROW_COUNT)?.toInt()?.let { max(1, it) },
                byRow = getBoolean(BY_ROW)
            )
        }
    }

    private class ColorBarConfig(opts: Map<String, Any>) : GuideConfig(opts) {

        override fun createGuideOptionsIntern(): GuideOptions {
            return ColorBarOptions(
                width = getDouble(WIDTH),
                height = getDouble(HEIGHT),
                binCount = getInteger(BIN_COUNT)
            )
        }
    }

    companion object {
        fun create(guide: Any): GuideConfig {
            // guide - name (legend/color-bar)
            //        or
            //        map with guide options
            if (guide is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val guideOptions = guide as Map<String, Any>
                return createForName(
                    ConfigUtil.featureName(
                        guideOptions
                    ), guideOptions
                )
            }
            return createForName(guide.toString(), HashMap())
        }

        private fun createForName(name: String, guideOptions: Map<String, Any>): GuideConfig {
            return when (name) {
                COLOR_BAR, COLOR_BAR_GB -> ColorBarConfig(guideOptions)
                NONE -> GuideNoneConfig()
                LEGEND -> LegendConfig(guideOptions)
                else -> error("Unknown guide name: $name")
            }
        }
    }
}
