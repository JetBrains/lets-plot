/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.simple

import jetbrains.datalore.vis.swing.ApplicationContext
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.PlotPanel
import java.awt.Component

class SimplePlotPanel(
    plotComponentProvider: PlotComponentProvider,
    preferredSizeFromPlot: Boolean,
    applicationContext: ApplicationContext = SimpleApplicationContext()
) :
    PlotPanel(
        plotComponentProvider,
        preferredSizeFromPlot,
        applicationContext
    ) {
    override fun handleChildRemoved(child: Component) {
        // Nothing is needed.
    }
}