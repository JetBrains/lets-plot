package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.config.Option.Theme.AXIS_LINE
import jetbrains.datalore.plot.config.Option.Theme.AXIS_TEXT
import jetbrains.datalore.plot.config.Option.Theme.AXIS_TICKS
import jetbrains.datalore.plot.config.Option.Theme.AXIS_TITLE
import jetbrains.datalore.plot.config.OptionsAccessor

class AxisThemeConfig private constructor(options: Map<*, *>, defOptions: Map<*, *>, private val myX: Boolean) : OptionsAccessor(options, defOptions), AxisTheme {

    private fun defTheme(): AxisTheme {
        return if (myX)
            ThemeConfig.DEF.axisX()
        else
            ThemeConfig.DEF.axisY()
    }

    private fun optionSuffix(): String {
        return if (myX)
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

    override fun showTickLabels(): Boolean {
        return !disabled(AXIS_TEXT)
    }

    override fun showTitle(): Boolean {
        return !disabled(AXIS_TITLE)
    }

    override fun lineWidth(): Double {
        return defTheme().lineWidth()
    }

    override fun tickMarkWidth(): Double {
        return defTheme().tickMarkWidth()
    }

    override fun tickMarkLength(): Double {
        return defTheme().tickMarkLength()
    }

    override fun tickMarkPadding(): Double {
        return defTheme().tickMarkPadding()
    }

    private fun getViewElementConfig(optionName: String): ViewElementConfig {
        checkState(hasApplicable(optionName), "option '$optionName' is not specified")
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
        fun X(options: Map<*, *>, defOptions: Map<*, *>): AxisThemeConfig {
            return AxisThemeConfig(options, defOptions, true)
        }

        fun Y(options: Map<*, *>, defOptions: Map<*, *>): AxisThemeConfig {
            return AxisThemeConfig(options, defOptions, false)
        }
    }
}
