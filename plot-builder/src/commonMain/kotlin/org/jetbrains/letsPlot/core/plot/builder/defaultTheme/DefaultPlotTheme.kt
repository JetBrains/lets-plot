/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TAG
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_MESSAGE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_SUBTITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TITLE
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.TagLocation
import org.jetbrains.letsPlot.core.plot.base.theme.TitlePosition
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TAG_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TAG_LOCATION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_INSET
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TAG_PREFIX
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TAG_SUFFIX
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE_POSITION

internal class DefaultPlotTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), PlotTheme {

    internal val backgroundKey = listOf(PLOT_BKGR_RECT, RECT)
    internal val titleKey = listOf(PLOT_TITLE, TITLE, TEXT)
    internal val subtitleKey = listOf(PLOT_SUBTITLE, TITLE, TEXT)
    internal val captionKey = listOf(PLOT_CAPTION, TITLE, TEXT)
    internal val tagKey = listOf(PLOT_TAG, TITLE, TEXT)
    internal val messagesKey = listOf(PLOT_MESSAGE)
    private val marginKey = listOf(PLOT_MARGIN)
    private val insetKey = listOf(PLOT_INSET)

    override fun showBackground(): Boolean {
        return !isElemBlank(backgroundKey)
    }

    override fun backgroundColor(): Color {
        return getColor(getElemValue(backgroundKey), Elem.COLOR)
    }

    override fun backgroundFill(): Color {
        return getColor(getElemValue(backgroundKey), Elem.FILL)
    }

    override fun backgroundStrokeWidth(): Double {
        return getNumber(getElemValue(backgroundKey), Elem.SIZE)
    }

    override fun backgroundLineType() = getLineType(getElemValue(backgroundKey))

    override fun titleStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(titleKey))
    }

    override fun subtitleStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(subtitleKey))
    }

    override fun captionStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(captionKey))
    }

    override fun tagStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(tagKey))
    }

    override fun textColor(): Color {
        return getColor(getElemValue(listOf(TEXT)), Elem.COLOR)
    }

    override fun textStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(listOf(TEXT)))
    }

    override fun showTitle() = !isElemBlank(titleKey)
    override fun showSubtitle() = !isElemBlank(subtitleKey)
    override fun showCaption() = !isElemBlank(captionKey)
    override fun showTag() = !isElemBlank(tagKey)

    override fun titleJustification(): TextJustification {
        return getTextJustification(getElemValue(titleKey))
    }

    override fun subtitleJustification(): TextJustification {
        return getTextJustification(getElemValue(subtitleKey))
    }

    override fun captionJustification(): TextJustification {
        return getTextJustification(getElemValue(captionKey))
    }

    override fun tagJustification(): TextJustification {
        return getTextJustification(getElemValue(tagKey))
    }

    override fun titleMargins() = getMargins(getElemValue(titleKey))

    override fun subtitleMargins() = getMargins(getElemValue(subtitleKey))

    override fun captionMargins() = getMargins(getElemValue(captionKey))

    override fun tagMargins() = getMargins(getElemValue(tagKey))

    override fun plotMargins() = getMargins(getElemValue(marginKey))

    override fun plotInset() = getPadding(getElemValue(insetKey))

    override fun titlePosition(): TitlePosition {
        return getValue(PLOT_TITLE_POSITION) as TitlePosition
    }

    override fun captionPosition(): TitlePosition {
        return getValue(PLOT_CAPTION_POSITION) as TitlePosition
    }

    override fun tagPosition(): DoubleVector {
        val pair = getValue(PLOT_TAG_POSITION) as Pair<Number, Number>
        return DoubleVector(pair)
    }

    override fun tagLocation(): TagLocation {
        return getValue(PLOT_TAG_LOCATION) as TagLocation
    }

    override fun tagPrefix(): String {
        return get(PLOT_TAG_PREFIX) as? String ?: ""
    }

    override fun tagSuffix(): String {
        return get(PLOT_TAG_SUFFIX) as? String ?: ""
    }

    override fun showMessage(): Boolean {
        return !isElemBlank(messagesKey)
    }
}