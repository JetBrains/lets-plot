/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.FeatureSwitch
import org.jetbrains.letsPlot.core.plot.builder.interact.toolbox.PlotToolbox
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

class PlotContainer constructor(
    private val svgRoot: PlotSvgRoot,
) : Disposable {

    val svg: SvgSvgElement
        get() = svgRoot.svg

    val liveMapFigures: List<SomeFig>
        get() = svgRoot.liveMapFigures

    val isLiveMap: Boolean
        get() = svgRoot.isLiveMap

    val mouseEventPeer: MouseEventPeer
        get() = plot.mouseEventPeer

    private val plot: PlotSvgComponent = svgRoot.plot
    private var registrations = CompositeRegistration()

    init {
        if (plot.interactionsEnabled) {
            plot.interactor = PlotInteractor(
                decorationLayer = svgRoot.decorationLayer,
                mouseEventPeer = mouseEventPeer,
                plotSize = plot.figureSize,    // ToDo: svgRoot.bounds.dimension
                flippedAxis = plot.flippedAxis,
                theme = plot.theme,
                plotContext = plot.plotContext
            )

            if (FeatureSwitch.PLOT_VIEW_TOOLBOX) {
                registrations.add(
                    addViewToolbox(plot.interactor as PlotInteractor)
                )
            }
        }

        svgRoot.ensureContentBuilt()
    }

    override fun dispose() {
        registrations.remove()
        svgRoot.clearContent()
    }


    companion object {
        private fun addViewToolbox(interactor: PlotInteractor): Registration {
            return Registration.from(PlotToolbox(interactor))
        }
    }
}
