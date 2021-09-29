/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.Interactor
import jetbrains.datalore.vis.svg.SvgGElement

class PlotContainer(
    plot: PlotSvgComponent,
    plotSize: DoubleVector
) : PlotContainerPortable(plot, plotSize) {
    private val myDecorationLayer = SvgGElement()

    val mouseEventPeer: jetbrains.datalore.plot.builder.event.MouseEventPeer
        get() = plot.mouseEventPeer


    init {
        if (plot.interactionsEnabled) {
            plot.interactor = Interactor(myDecorationLayer, plot, mouseEventPeer)
        }
    }

    override fun buildContent() {
        super.buildContent()
        if (plot.interactionsEnabled) {
            svg.children().add(myDecorationLayer)
        }
    }

    override fun clearContent() {
        myDecorationLayer.children().clear()
        super.clearContent()
    }
}
