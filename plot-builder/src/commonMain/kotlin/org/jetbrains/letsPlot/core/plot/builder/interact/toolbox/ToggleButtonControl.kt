/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.toolbox

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

class ToggleButtonControl(
    private val uncheckedContent: SvgElement,
    private val checkedContent: SvgElement
) : SvgControl() {

    private var isChecked = false
    private var clickHandler: (() -> Unit)? = null
    private var toggleHandler: ((Boolean) -> Unit)? = null

    override var size: DoubleVector = DoubleVector(24.0, 24.0)
        set(value) {
            field = value
            updateContent()
        }

    override fun buildUiComponent(rootGroup: SvgGElement) {
        super.buildUiComponent(rootGroup)
        updateContent()
    }

    override fun onMouseClicked(e: MouseEvent) {
        // Click only triggers event, not toggling
        clickHandler?.invoke()

        toggleHandler?.let {
            isChecked = !isChecked
            it.invoke(isChecked)
            updateContent()
        }
    }

    private fun updateContent() {
        (checkedContent.children() + uncheckedContent.children())
            .mapNotNull { it as? SvgRectElement }
            .forEach {
                it.setAttribute(SvgConstants.WIDTH, size.x.toString())
                it.setAttribute(SvgConstants.HEIGHT, size.y.toString())
            }

        svgRoot.children().clear()
        if (isChecked) {
            svgRoot.children().add(checkedContent)
        } else {
            svgRoot.children().add(uncheckedContent)
        }
    }

    fun onClick(function: () -> Unit) {
        clickHandler = function
    }

    fun onToggleClick(function: (Boolean) -> Unit) {
        toggleHandler = function
    }
}
