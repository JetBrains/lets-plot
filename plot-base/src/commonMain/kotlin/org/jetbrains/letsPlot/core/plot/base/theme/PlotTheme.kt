/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.Margins
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification

interface PlotTheme {
    fun showBackground(): Boolean
    fun showMessage(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
    fun titleStyle(): ThemeTextStyle
    fun subtitleStyle(): ThemeTextStyle
    fun captionStyle(): ThemeTextStyle

    fun textColor(): Color
    fun textStyle(): ThemeTextStyle

    fun showTitle(): Boolean
    fun showSubtitle(): Boolean
    fun showCaption(): Boolean

    fun titleJustification(): TextJustification
    fun subtitleJustification(): TextJustification
    fun captionJustification(): TextJustification

    fun titleMargins(): Margins
    fun subtitleMargins(): Margins
    fun captionMargins(): Margins
    fun plotMargins(): Margins
}
