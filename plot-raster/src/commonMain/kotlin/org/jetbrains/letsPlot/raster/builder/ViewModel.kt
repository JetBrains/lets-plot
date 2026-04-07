/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builder

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.child
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.CompositeToolEventDispatcher
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

internal sealed class ViewModel(
    val svg: SvgSvgElement,
) : Disposable {
    val mouseEventPeer: MouseEventPeer = MouseEventPeer()
    abstract val toolEventDispatcher: ToolEventDispatcher

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

internal class SimpleModel(svg: SvgSvgElement) : ViewModel(svg) {
    override val toolEventDispatcher: ToolEventDispatcher
        get() = UnsupportedToolEventDispatcher()

    override fun dispose() {}
}

internal class SinglePlotModel(
    svg: SvgSvgElement,
    override val toolEventDispatcher: ToolEventDispatcher,
    private val registration: Registration
) : ViewModel(svg) {
    override fun dispose() = registration.dispose()
}

internal class CompositeFigureModel(svg: SvgSvgElement, private val isDeck: Boolean = false) : ViewModel(svg) {
    private val children = ArrayList<Pair<ViewModel, Disposable>>()
    private var _toolEventDispatcher: ToolEventDispatcher? = null

    override val toolEventDispatcher: ToolEventDispatcher
        get() {
            if (_toolEventDispatcher == null) {
                _toolEventDispatcher = CompositeToolEventDispatcher(children.map { (vm, _) -> vm.toolEventDispatcher }, isDeck = isDeck)
            }
            return _toolEventDispatcher!!
        }

    fun addChild(childModel: ViewModel, childBounds: DoubleRectangle) {
        val childMouseEventSource = mouseEventPeer.child { childBounds }
        val eventSourceReg = childModel.mouseEventPeer.addEventSource(childMouseEventSource)

        children.add(childModel to Registration.from(childModel, childMouseEventSource, eventSourceReg))
        _toolEventDispatcher = null
    }

    fun assembleAsRoot() {
        children.forEach { (vm, _) -> vm.collect(dest = this.svg) }
    }

    override fun collect(dest: SvgSvgElement) {
        dest.children().add(this.svg)
        children.forEach { (vm, _) -> vm.collect(dest) }
    }

    override fun dispose() {
        children.forEach { (_, finalizer) ->
            finalizer.dispose()
        }
        children.clear()
    }
}