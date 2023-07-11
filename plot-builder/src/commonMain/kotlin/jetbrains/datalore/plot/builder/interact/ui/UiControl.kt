/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.ui

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

abstract class UiControl {

    private var parent: UiControl? = null
    private val svgComponent = object : SvgComponent() {
        override fun buildComponent() {
            buildUiComponent(rootGroup)
        }
    }

    val svgRoot get() = svgComponent.rootGroup

    val children = mutableListOf<UiControl>()

    abstract val size: DoubleVector

    var origin: DoubleVector = DoubleVector.ZERO
        set(value) {
            field = value
            svgComponent.moveTo(value)
        }

    val bbox: DoubleRectangle
        get() {
        var p = parent
        var absolutOrigin: DoubleVector = origin
        while (p != null) {
            absolutOrigin = absolutOrigin.add(p.origin)
            p = p.parent
        }

        return DoubleRectangle(absolutOrigin, size)
    }


    fun addChildren(control: UiControl) {
        control.parent = this
        children.add(control)
    }

    open fun buildUiComponent(rootGroup: SvgGElement) {
        children.forEach { rootGroup.children().add(it.svgRoot) }

        // TODO: doens't work in JVM, works fine in DOM
        //svgRoot.addEventHandler(SvgEventSpec.MOUSE_MOVE, object : SvgEventHandler<MouseEvent> {
        //    override fun handle(node: SvgNode, e: MouseEvent) {
        //        println("svg: mouse moved")
        //    }
        //})
    }

    fun dispatch(spec: MouseEventSpec, e: MouseEvent) {
        when (spec) {
            MOUSE_ENTERED -> onMouseEntered(e)
            MOUSE_LEFT -> onMouseLeft(e)
            MOUSE_MOVED -> onMouseMoved(e)
            MOUSE_DRAGGED -> onMouseDragged(e)
            MOUSE_CLICKED -> onMouseClicked(e)
            MOUSE_DOUBLE_CLICKED -> onMouseDoubleClicked(e)
            MOUSE_PRESSED -> onMousePressed(e)
            MOUSE_RELEASED -> onMouseReleased(e)
        }
    }

    open fun onMouseClicked(e: MouseEvent) {}
    open fun onMouseEntered(e: MouseEvent) {}
    open fun onMouseLeft(e: MouseEvent) {}
    open fun onMouseMoved(e: MouseEvent) {}
    open fun onMouseDragged(e: MouseEvent) {}
    open fun onMousePressed(e: MouseEvent) {}
    open fun onMouseReleased(e: MouseEvent) {}
    open fun onMouseDoubleClicked(e: MouseEvent) {}
}
