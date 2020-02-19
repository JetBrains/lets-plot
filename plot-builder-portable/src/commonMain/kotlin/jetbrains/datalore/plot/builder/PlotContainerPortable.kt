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
import jetbrains.datalore.vis.svg.SvgCssResource
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
