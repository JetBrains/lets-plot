/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgCssResource
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgSvgElement

/**
 *  This class only handles static SVG. (no interactions)
 */
class PlotSvgContainer(
    val plot: PlotSvgComponent,
    val bounds: DoubleRectangle
) {
    val svg: SvgSvgElement = SvgSvgElement()

    val liveMapFigures: List<SomeFig>
        get() = plot.liveMapFigures

    val isLiveMap: Boolean
        get() = plot.liveMapFigures.isNotEmpty()

    private val decorationLayerId = SvgUID.get(DECORATION_LAYER_ID_PREFIX)
    val decorationLayer = SvgGElement().apply {
        id().set(decorationLayerId)
    }

    private var isContentBuilt: Boolean = false

    init {
        svg.addClass(Style.PLOT_CONTAINER)
        setSvgSize(bounds.dimension)
        plot.resize(bounds.dimension)
    }

    fun ensureContentBuilt() {
        if (!isContentBuilt) {
            buildContent()
        }
    }

    fun resize(plotSize: DoubleVector) {
        if (isContentBuilt) {
            throw IllegalStateException(
                "The plot SVG container is already built." +
                        "\nPlease, call `clearContent()` before `resize()`."
            )
        }

        if (plotSize.x <= 0 || plotSize.y <= 0) return
        if (plotSize == plot.plotSize) return

        setSvgSize(plotSize)
        plot.resize(plotSize)
    }

    private fun buildContent() {
        check(!isContentBuilt)
        isContentBuilt = true

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

    fun clearContent() {
        if (isContentBuilt) {
            isContentBuilt = false

            svg.children().clear()
            decorationLayer.children().clear()
            plot.clear()
        }
    }

    private fun setSvgSize(size: DoubleVector) {
        svg.width().set(size.x)
        svg.height().set(size.y)
    }

    private companion object {
        const val PLOT_ID_PREFIX = "p"
        const val DECORATION_LAYER_ID_PREFIX = "d"
    }
}
