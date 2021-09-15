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
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_BACKDROP
import jetbrains.datalore.vis.svg.SvgCssResource
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement

/**
 *  This class only handles static SVG. (no interactions)
 */
open class PlotContainerPortable(
    protected val plot: Plot,
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

        // Add Plot background.

        // Batik doesn't seem to support any styling (via 'style' element or 'style' attribute)
        // of root <svg>-element.
        // Therefore the 'backdrop' rectungle is necessary.
        val backdrop = SvgRectElement()
        backdrop.addClass(PLOT_BACKDROP)

        // Jfx Scene ignores these values (percentage is not supported).
        // In the case of Jfx Scene the 'backdrop' rectungle has visibility=hidden
        // and styling of the root <svg>-element is used.
        // (see: 'resources/svgMapper/jfx/plot.css' in plot-builder)
        backdrop.setAttribute("width", "100%")
        backdrop.setAttribute("height", "100%")

        // This works for DOM / Batik but ignored by JFX Scene
        // Also, 'width'/'height' attributes are required by Batik.
        // (or it fails with org.apache.batik.bridge.BridgeException)
//        backdrop.setAttribute(SVG_STYLE_ATTRIBUTE, "width: 100%; height: 100%")

        svg.children().add(backdrop)
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
