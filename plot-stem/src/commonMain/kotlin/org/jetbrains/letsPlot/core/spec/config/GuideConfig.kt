/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.assemble.ColorBarOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendOptions
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Guide.COLOR_BAR
import org.jetbrains.letsPlot.core.spec.Option.Guide.COLOR_BAR_GB
import org.jetbrains.letsPlot.core.spec.Option.Guide.ColorBar.BIN_COUNT
import org.jetbrains.letsPlot.core.spec.Option.Guide.ColorBar.HEIGHT
import org.jetbrains.letsPlot.core.spec.Option.Guide.ColorBar.WIDTH
import org.jetbrains.letsPlot.core.spec.Option.Guide.LEGEND
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.BY_ROW
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.COL_COUNT
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.OVERRIDE_AES
import org.jetbrains.letsPlot.core.spec.Option.Guide.Legend.ROW_COUNT
import org.jetbrains.letsPlot.core.spec.Option.Guide.NONE
import org.jetbrains.letsPlot.core.spec.Option.Guide.REVERSE
import org.jetbrains.letsPlot.core.spec.Option.Guide.TITLE
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import kotlin.math.max

abstract class GuideConfig private constructor(opts: Map<String, Any>) : OptionsAccessor(opts) {

    fun createGuideOptions(aopConversion: AesOptionConversion): GuideOptions {
        val options = createGuideOptionsIntern(aopConversion)
        return options
            .withTitle(getString(TITLE))
            .withReverse(getBoolean(REVERSE))
    }

    protected abstract fun createGuideOptionsIntern(aopConversion: AesOptionConversion): GuideOptions

    private class GuideNoneConfig internal constructor() : GuideConfig(emptyMap()) {

        override fun createGuideOptionsIntern(aopConversion: AesOptionConversion): GuideOptions {
            return GuideOptions.NONE
        }
    }

    private class LegendConfig internal constructor(opts: Map<String, Any>) : GuideConfig(opts) {

        override fun createGuideOptionsIntern(aopConversion: AesOptionConversion): GuideOptions {
            return LegendOptions(
                colCount = getDouble(COL_COUNT)?.toInt()?.let { max(1, it) },
                rowCount = getDouble(ROW_COUNT)?.toInt()?.let { max(1, it) },
                byRow = getBoolean(BY_ROW),
                overrideAesValues = initValues(
                    OptionsAccessor(getMap(OVERRIDE_AES)),
                    aopConversion
                )
            )
        }

        private fun initValues(
            layerOptions: OptionsAccessor,
            aopConversion: AesOptionConversion
        ): Map<Aes<*>, Any> {
            val result = HashMap<Aes<*>, Any>()
            Option.Mapping.REAL_AES_OPTION_NAMES
                .filter(layerOptions::has)
                .associateWith(Option.Mapping::toAes)
                .forEach { (option, aes) ->
                    val optionValue = layerOptions.getSafe(option)
                    val value = if (optionValue is List<*>) {
                        optionValue.map { aopConversion.apply(aes, it) }
                    } else {
                        aopConversion.apply(aes, optionValue)
                    } ?: throw IllegalArgumentException("Can't convert to '$option' value: $optionValue")
                    result[aes] = value
                }
            return result
        }
    }

    private class ColorBarConfig(opts: Map<String, Any>) : GuideConfig(opts) {

        override fun createGuideOptionsIntern(aopConversion: AesOptionConversion): GuideOptions {
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
