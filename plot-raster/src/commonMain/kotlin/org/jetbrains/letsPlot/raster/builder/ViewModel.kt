/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builder

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.child
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.toDoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

internal sealed class ViewModel(
    val svg: SvgSvgElement,
    val toolEventDispatcher: ToolEventDispatcher,
) : Disposable {
    val mouseEventPeer: MouseEventPeer = MouseEventPeer()
    internal abstract val bounds: Rectangle

    @Suppress("unused")
    fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        toolEventDispatcher.activateInteractions(origin, interactionSpecList)
    }

    @Suppress("unused")
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
) : ViewModel(svg, toolEventDispatcher) {
    override val bounds: Rectangle
        get() = throw IllegalStateException("Not supported: SimpleModel.bounds")

    override fun dispose() {}
}

internal class SinglePlotModel(
    svg: SvgSvgElement,
    toolEventDispatcher: ToolEventDispatcher,
    override val bounds: Rectangle,
    private val registration: Registration
) : ViewModel(svg, toolEventDispatcher) {
    override fun dispose() {
        registration.dispose()
    }
}

internal class CompositeFigureModel(
    svg: SvgSvgElement,
    toolEventDispatcher: ToolEventDispatcher,
    override val bounds: Rectangle,
) : ViewModel(svg, toolEventDispatcher) {
    private val children = ArrayList<ViewModel>()

    fun addChildFigure(childModel: ViewModel) {
        children.add(childModel)
        val childMouseEventSource = mouseEventPeer.child { childModel.bounds.toDoubleRectangle() }
        childModel.mouseEventPeer.addEventSource(childMouseEventSource)
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