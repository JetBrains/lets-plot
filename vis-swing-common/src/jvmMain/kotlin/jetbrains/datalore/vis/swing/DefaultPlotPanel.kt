/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import java.awt.Component


/**
 * Inherited by the IdeaPlotPanel class in IDEA plugin.
 */
open class DefaultPlotPanel(
    plotComponentProvider: PlotComponentProvider,
    preferredSizeFromPlot: Boolean,
    refreshRate: Int,  // ms
    applicationContext: ApplicationContext
) :
    PlotPanel(
        plotComponentProvider,
        preferredSizeFromPlot,
        refreshRate = refreshRate,
        applicationContext = applicationContext
    ) {

    /**
     * Overridden in IDEA plugin.
     *  - checks for an instance of `com.intellij.openapi.Disposable`
     *  - invokes `com.intellij.openapi.Disposer.dispose(child)`
     */
    override fun handleChildRemoved(child: Component) {
        // Nothing is needed.
    }
}