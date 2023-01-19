/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.builder.interact.Interactor
import jetbrains.datalore.plot.builder.interact.PlotToolbox
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
            plot.interactor = Interactor(
                decorationLayer = myDecorationLayer,
                mouseEventPeer = mouseEventPeer,
                plotSize = plot.plotSize,
                flippedAxis = plot.flippedAxis,
                theme = plot.theme,
                plotContext = plot.plotContext
            )

            if (FeatureSwitch.PLOT_VIEW_TOOLBOX) {
                reg(addViewToolbox(plot.interactor as Interactor))
            }
        }
    }

    override fun buildContent() {
        super.buildContent()
        if (plot.interactionsEnabled) {
            myDecorationLayer.id().set(decorationLayerId)
            svg.children().add(myDecorationLayer)
        }
    }

    override fun clearContent() {
        myDecorationLayer.children().clear()
        super.clearContent()
    }

    companion object {
        private fun addViewToolbox(interactor: Interactor): Registration {
            return Registration.from(PlotToolbox(interactor))
        }
    }
}
