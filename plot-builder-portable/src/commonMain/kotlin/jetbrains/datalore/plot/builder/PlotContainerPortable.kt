/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgCssResource
import jetbrains.datalore.vis.svg.SvgSvgElement

/**
 *  This class only handles static SVG. (no interactions)
 */
open class PlotContainerPortable(
    protected val plot: PlotSvgComponent,
    plotSize: DoubleVector
) {

    val svg: SvgSvgElement = SvgSvgElement()

    val liveMapFigures: List<SomeFig>
        get() = plot.liveMapFigures

    val isLiveMap: Boolean
        get() = plot.liveMapFigures.isNotEmpty()


    private var myContentBuilt: Boolean = false
    private var myRegistrations = CompositeRegistration()

    init {
        svg.addClass(Style.PLOT_CONTAINER)
        setSvgSize(plotSize)
        plot.resize(plotSize)
    }

    fun ensureContentBuilt() {
        if (!myContentBuilt) {
            buildContent()
        }
    }

    fun resize(plotSize: DoubleVector) {
        if (plotSize.x <= 0 || plotSize.y <= 0) return
        if (plotSize == plot.plotSize) return

        // Invalidate
        clearContent()
        setSvgSize(plotSize)
        plot.resize(plotSize)
    }

//    private fun revalidateContent() {
//        if (myContentBuilt) {
//            clearContent()
//            buildContent()
//        }
//    }

    protected open fun buildContent() {
        check(!myContentBuilt)
        myContentBuilt = true

        svg.setStyle(object : SvgCssResource {
            override fun css(): String {
                return Style.css
            }
        })

        // Notes on plot background.
        // (No more actual as the background rect is now added in PlotSvgComponent)

        // 1.
        // Batik doesn't seem to support any styling (via 'style' element or 'style' attribute)
        // of root <svg>-element.

        // 2.
        // Jfx Scene ignores size values set in % (percentage is not supported).
        // Styling of the root <svg>-element can be done in an external css file.
        // (see: 'resources/svgMapper/jfx/plot.css' in plot-builder)

        svg.children().add(plot.rootGroup)
    }

    open fun clearContent() {
        if (myContentBuilt) {
            myContentBuilt = false

            svg.children().clear()
            plot.clear()
            myRegistrations.remove()
            myRegistrations = CompositeRegistration()
        }
    }

    protected fun reg(registration: Registration) {
        myRegistrations.add(registration)
    }

    private fun setSvgSize(size: DoubleVector) {
        svg.width().set(size.x)
        svg.height().set(size.y)
    }
}
