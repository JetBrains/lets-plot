/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builderLW

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.CanvasEventDispatcher

sealed class ViewModel(
    val svg: SvgSvgElement,
    val toolEventDispatcher: ToolEventDispatcher,
    val eventDispatcher: CanvasEventDispatcher,
) : Disposable {
    internal abstract val bounds: Rectangle

    fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>) {
        toolEventDispatcher.activateInteractions(origin, interactionSpecList)
    }

    fun deactivateInteractions(origin: String) {
        toolEventDispatcher.deactivateInteractions(origin)
    }

    internal open fun collect(dest: SvgSvgElement) {
        dest.children().add(svg)
    }
}

internal class SimpleModel(
    svg: SvgSvgElement,
    toolEventDispatcher: ToolEventDispatcher,
) : ViewModel(
    svg,
    toolEventDispatcher,
    eventDispatcher = object : CanvasEventDispatcher {
        override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {} // ignore events
        override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
            return Registration.EMPTY
        }
    }
) {
    override val bounds: Rectangle
        get() = throw IllegalStateException("Not supported: SimpleModel.bounds")

    override fun dispose() {}
}

internal class SinglePlotModel(
    svg: SvgSvgElement,
    eventDispatcher: CanvasEventDispatcher,
    toolEventDispatcher: ToolEventDispatcher,
    override val bounds: Rectangle,
    private val registration: Registration
) : ViewModel(svg, toolEventDispatcher, eventDispatcher) {

    override fun dispose() {
        registration.dispose()
    }
}

internal class CompositeFigureModel(
    svg: SvgSvgElement,
    toolEventDispatcher: ToolEventDispatcher,
    override val bounds: Rectangle,
) : ViewModel(svg, toolEventDispatcher, CompositeFigureEventDispatcher()) {
    private val children = ArrayList<ViewModel>()

    fun addChildFigure(childModel: ViewModel) {
        children.add(childModel)
        (eventDispatcher as CompositeFigureEventDispatcher).addEventDispatcher(
            bounds = childModel.bounds,
            eventDispatcher = childModel.eventDispatcher
        )
    }

    fun assembleAsRoot() {
        children.forEach { it.collect(dest = this.svg) }
    }

    override fun collect(dest: SvgSvgElement) {
        dest.children().add(this.svg)
        children.forEach { it.collect(dest) }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
        children.clear()
    }
}