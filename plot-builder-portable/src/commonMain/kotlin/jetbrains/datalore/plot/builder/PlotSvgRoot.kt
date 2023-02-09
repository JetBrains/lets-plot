/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgCssResource
import jetbrains.datalore.vis.svg.SvgGElement

/**
 *  This class only handles static SVG. (no interactions)
 */
class PlotSvgRoot(
    val plot: PlotSvgComponent,
    val liveMapCursorServiceConfig: Any?,
    bounds: DoubleRectangle
) : FigureSvgRoot(bounds) {

    val liveMapFigures: List<SomeFig>
        get() = plot.liveMapFigures

    val isLiveMap: Boolean
        get() = plot.liveMapFigures.isNotEmpty()


    private val decorationLayerId = SvgUID.get(DECORATION_LAYER_ID_PREFIX)
    val decorationLayer = SvgGElement().apply {
        id().set(decorationLayerId)
    }

    init {
        plot.plotSize = bounds.dimension
    }

    protected override fun buildFigureContent() {
        val id = SvgUID.get(PLOT_ID_PREFIX)

        svg.setStyle(object : SvgCssResource {
            override fun css(): String {
                return Style.generateCSS(plot.styleSheet, id, decorationLayerId)
            }
        })

        plot.rootGroup.id().set(id)

        // Notes on plot background.
        // (No more actual as the background rect is now added in PlotSvgComponent)

        // 1.
        // Batik doesn't seem to support any styling (via 'style' element or 'style' attribute)
        // of root <svg>-element.

        // 2.
        // Jfx Scene ignores size values set in % (percentage is not supported).
        // Styling of the root <svg>-element can be done in an external css file.

        svg.children().add(plot.rootGroup)

        if (plot.interactionsEnabled) {
            svg.children().add(decorationLayer)
        }
    }

    protected override fun clearFigureContent() {
        decorationLayer.children().clear()
        plot.clear()
    }

    private companion object {
        const val PLOT_ID_PREFIX = "p"
        const val DECORATION_LAYER_ID_PREFIX = "d"
    }
}
