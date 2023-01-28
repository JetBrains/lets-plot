/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.builder.interact.Interactor
import jetbrains.datalore.plot.builder.interact.PlotToolbox
import jetbrains.datalore.vis.svg.SvgSvgElement

class PlotContainer(
    private val plotSvgContainer: PlotSvgContainer,
) {
    val svg: SvgSvgElement
        get() = plotSvgContainer.svg

    val liveMapFigures: List<SomeFig>
        get() = plotSvgContainer.liveMapFigures

    val isLiveMap: Boolean
        get() = plotSvgContainer.isLiveMap

    val mouseEventPeer: jetbrains.datalore.plot.builder.event.MouseEventPeer
        get() = plot.mouseEventPeer

    private val plot: PlotSvgComponent = plotSvgContainer.plot
    private var registrations = CompositeRegistration()

    init {
        if (plot.interactionsEnabled) {
            plot.interactor = Interactor(
                decorationLayer = plotSvgContainer.decorationLayer,
                mouseEventPeer = mouseEventPeer,
                plotSize = plot.plotSize,
                flippedAxis = plot.flippedAxis,
                theme = plot.theme,
                plotContext = plot.plotContext
            )

            if (FeatureSwitch.PLOT_VIEW_TOOLBOX) {
                registrations.add(
                    addViewToolbox(plot.interactor as Interactor)
                )
            }
        }
    }

    fun ensureContentBuilt() {
        plotSvgContainer.ensureContentBuilt()
    }

    fun clearContent() {
        registrations.remove()
        registrations = CompositeRegistration()
        plotSvgContainer.clearContent()
    }

    fun resize(plotSize: DoubleVector) {
        plotSvgContainer.resize(plotSize)
    }


    companion object {
        private fun addViewToolbox(interactor: Interactor): Registration {
            return Registration.from(PlotToolbox(interactor))
        }
    }
}
