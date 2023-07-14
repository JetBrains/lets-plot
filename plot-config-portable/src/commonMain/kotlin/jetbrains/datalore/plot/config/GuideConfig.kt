/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.intern.function.Runnable
import org.jetbrains.letsPlot.core.plot.builder.assemble.ColorBarOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendOptions
import jetbrains.datalore.plot.config.Option.Guide.COLOR_BAR
import jetbrains.datalore.plot.config.Option.Guide.COLOR_BAR_GB
import jetbrains.datalore.plot.config.Option.Guide.ColorBar.BIN_COUNT
import jetbrains.datalore.plot.config.Option.Guide.ColorBar.HEIGHT
import jetbrains.datalore.plot.config.Option.Guide.ColorBar.WIDTH
import jetbrains.datalore.plot.config.Option.Guide.LEGEND
import jetbrains.datalore.plot.config.Option.Guide.Legend.BY_ROW
import jetbrains.datalore.plot.config.Option.Guide.Legend.COL_COUNT
import jetbrains.datalore.plot.config.Option.Guide.Legend.ROW_COUNT
import jetbrains.datalore.plot.config.Option.Guide.NONE
import jetbrains.datalore.plot.config.Option.Guide.REVERSE

abstract class GuideConfig private constructor(opts: Map<String, Any>) : OptionsAccessor(opts) {

    fun createGuideOptions(): GuideOptions {
        val options = createGuideOptionsIntern()
        options.isReverse = getBoolean(REVERSE)
        return options
    }

    protected abstract fun createGuideOptionsIntern(): GuideOptions

    protected fun trySafe(r: Runnable) {
        try {
            r.run()
        } catch (ignored: RuntimeException) {
            // ok
        }

    }


    private class GuideNoneConfig internal constructor() : GuideConfig(emptyMap()) {

        override fun createGuideOptionsIntern(): GuideOptions {
            return GuideOptions.NONE
        }
    }

    private class LegendConfig internal constructor(opts: Map<String, Any>) : GuideConfig(opts) {

        override fun createGuideOptionsIntern(): GuideOptions {
            val options = LegendOptions()
            trySafe(object : Runnable {
                override fun run() {
                    options.colCount = getDouble(COL_COUNT)!!.toInt()
                }

            })
            trySafe(object : Runnable {
                override fun run() {
                    options.rowCount = getDouble(ROW_COUNT)!!.toInt()
                }
            })

            options.isByRow = getBoolean(BY_ROW)
            return options
        }
    }

    private class ColorBarConfig(opts: Map<String, Any>) : GuideConfig(opts) {

        override fun createGuideOptionsIntern(): GuideOptions {
            val options = ColorBarOptions()
            trySafe(object : Runnable {
                override fun run() {
                    options.width = getDouble(WIDTH)
                }
            })
            trySafe(object : Runnable {
                override fun run() {
                    options.height = getDouble(HEIGHT)
                }
            })
            trySafe(object : Runnable {
                override fun run() {
                    options.binCount = getDouble(BIN_COUNT)!!.toInt()
                }
            })
            return options
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
