/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import java.awt.Dimension
import javax.swing.JComponent

interface PlotComponentProvider {
    fun getPreferredSize(containerSize: Dimension): Dimension
    fun createComponent(
        containerSize: Dimension?,
        specOverrideList: List<Map<String, Any>> = emptyList()
    ): JComponent
}