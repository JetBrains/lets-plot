/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.builder.interact.Interactor
import jetbrains.datalore.plot.builder.interact.PlotToolbox
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

    val mouseEventPeer: jetbrains.datalore.plot.builder.event.MouseEventPeer
        get() = plot.mouseEventPeer

    private val plot: PlotSvgComponent = svgRoot.plot
    private var registrations = CompositeRegistration()

    init {
        if (plot.interactionsEnabled) {
            plot.interactor = Interactor(
                decorationLayer = svgRoot.decorationLayer,
                mouseEventPeer = mouseEventPeer,
                plotSize = plot.figureSize,    // ToDo: svgRoot.bounds.dimension
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

        svgRoot.ensureContentBuilt()
    }

    override fun dispose() {
        registrations.remove()
        svgRoot.clearContent()
    }


    companion object {
        private fun addViewToolbox(interactor: Interactor): Registration {
            return Registration.from(PlotToolbox(interactor))
        }
    }
}
