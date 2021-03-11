/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import java.awt.Component


open class DefaultPlotPanel(
    plotComponentProvider: PlotComponentProvider,
    preferredSizeFromPlot: Boolean,
    refreshRate: Int,  // ms
    applicationContext: ApplicationContext
) : PlotPanel(
    plotComponentProvider,
    preferredSizeFromPlot,
    refreshRate = refreshRate,
    applicationContext = applicationContext
) {

    /**
     * Override for custom disposal of a child.
     */
    override fun handleChildRemoved(child: Component) {
        // Nothing is needed.
    }
}