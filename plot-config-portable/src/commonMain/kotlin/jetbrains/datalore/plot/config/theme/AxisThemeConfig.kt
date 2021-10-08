/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TEXT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TICKS
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TITLE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TOOLTIP
import jetbrains.datalore.plot.config.OptionsAccessor

class AxisThemeConfig private constructor(
    options: Map<String, Any>,
    defOptions: Map<String, Any>,
    private val isX: Boolean
) : OptionsAccessor(options, defOptions), AxisTheme {

    private fun defTheme(): AxisTheme {
        return if (isX)
            ThemeConfig.DEF.axisX()
        else
            ThemeConfig.DEF.axisY()
    }

    private fun optionSuffix(): String {
        return if (isX)
            "_x"
        else
            "_y"
    }

    override fun showLine(): Boolean {
        return !disabled(AXIS_LINE)
    }

    override fun showTickMarks(): Boolean {
        return !disabled(AXIS_TICKS)
    }

    override fun showLabels(): Boolean {
        return !disabled(AXIS_TEXT)
    }

    override fun showTitle(): Boolean {
        return !disabled(AXIS_TITLE)
    }

    override fun showTooltip(): Boolean {
        return !disabled(AXIS_TOOLTIP)
    }

    override fun lineWidth(): Double {
        return defTheme().lineWidth()
    }

    override fun tickMarkWidth(): Double {
        return defTheme().tickMarkWidth()
    }

    override fun lineColor(): Color {
        return defTheme().lineColor()
    }

    override fun tickMarkColor(): Color {
        return defTheme().tickMarkColor()
    }

    override fun labelColor(): Color {
        return defTheme().labelColor()
    }

    private fun getViewElementConfig(optionName: String): ViewElementConfig {
        check(hasApplicable(optionName)) { "option '$optionName' is not specified" }
        return ViewElementConfig.create(getApplicable(optionName)!!)
    }

    private fun disabled(option: String): Boolean {
        return hasApplicable(option) && getViewElementConfig(option).isBlank
    }

    fun hasApplicable(commonOption: String): Boolean {
        val axisOption = commonOption + optionSuffix()
        return has(axisOption) || has(commonOption)
    }

    fun getApplicable(commonOption: String): Any? {
        val axisOption = commonOption + optionSuffix()
        if (hasOwn(axisOption)) {
            return get(axisOption)
        }
        if (hasOwn(commonOption)) {
            return get(commonOption)
        }
        return if (has(axisOption)) {
            get(axisOption)
        } else get(commonOption)
    }

    companion object {
        fun X(options: Map<String, Any>, defOptions: Map<String, Any>): AxisThemeConfig {
            return AxisThemeConfig(options, defOptions, true)
        }

        fun Y(options: Map<String, Any>, defOptions: Map<String, Any>): AxisThemeConfig {
            return AxisThemeConfig(options, defOptions, false)
        }
    }
}
