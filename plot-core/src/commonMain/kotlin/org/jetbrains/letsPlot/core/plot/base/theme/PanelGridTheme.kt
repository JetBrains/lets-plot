/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color

interface PanelGridTheme {
    fun showMajor(): Boolean

    fun showMinor(): Boolean

    fun majorLineWidth(): Double

    fun minorLineWidth(): Double

    fun majorLineColor(): Color

    fun minorLineColor(): Color
}
