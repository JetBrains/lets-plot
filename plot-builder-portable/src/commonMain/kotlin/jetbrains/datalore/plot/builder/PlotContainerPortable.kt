/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_BACKDROP
import jetbrains.datalore.vis.svg.SvgCssResource
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import kotlin.math.max

/**
 *  This class only handles static SVG. (no interactions)
 */
open class PlotContainerPortable(
    protected val plot: Plot,
    private val preferredSize: ReadableProperty<DoubleVector>
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
        setSvgSize(preferredSize.get())

        plot.laidOutSize().addHandler(sizePropHandler { laidOutSize ->
            val newSvgSize = DoubleVector(
                max(preferredSize.get().x, laidOutSize.x),
                max(preferredSize.get().y, laidOutSize.y)
            )
            setSvgSize(newSvgSize)
        })

        preferredSize.addHandler(sizePropHandler { newPreferredSize ->
            if (newPreferredSize.x > 0 && newPreferredSize.y > 0) {
                revalidateContent()
            }
        })
    }

    fun ensureContentBuilt() {
        if (!myContentBuilt) {
            buildContent()
        }
    }

    private fun revalidateContent() {
        if (myContentBuilt) {
            clearContent()
            buildContent()
        }
    }

    protected open fun buildContent() {
        checkState(!myContentBuilt)
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

        plot.preferredSize().set(preferredSize.get())
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

    companion object {
        private fun sizePropHandler(block: (newValue: DoubleVector) -> Unit): EventHandler<PropertyChangeEvent<out DoubleVector>> {
            return object : EventHandler<PropertyChangeEvent<out DoubleVector>> {
                override fun onEvent(event: PropertyChangeEvent<out DoubleVector>) {
                    val newValue = event.newValue
                    if (newValue != null) {
                        block.invoke(newValue)
                    }
                }
            }
        }
    }
}
