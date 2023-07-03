/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim


object SvgSlimElements {
    val GROUP = "g"
    val LINE = "line"
    val CIRCLE = "circle"
    val RECT = "rect"
    val PATH = "path"

    private fun createElement(name: String): SlimBase {
        return ElementJava(name)
    }

    fun g(initialCapacity: Int): SvgSlimGroup {
        return GroupJava(initialCapacity)
    }

    fun g(initialCapacity: Int, transform: Any): SvgSlimGroup {
        return GroupJava(initialCapacity, transform)
    }

    fun line(x1: Double, y1: Double, x2: Double, y2: Double): SvgSlimShape {
        val element =
            createElement(LINE)
        element.setAttribute(SlimBase.x1, x1)
        element.setAttribute(SlimBase.y1, y1)
        element.setAttribute(SlimBase.x2, x2)
        element.setAttribute(SlimBase.y2, y2)
        return element
    }

    fun circle(cx: Double, cy: Double, r: Double): SvgSlimShape {
        val element =
            createElement(CIRCLE)
        element.setAttribute(SlimBase.cx, cx)
        element.setAttribute(SlimBase.cy, cy)
        element.setAttribute(SlimBase.r, r)
        return element
    }

    fun rect(x: Double, y: Double, width: Double, height: Double): SvgSlimShape {
        val element =
            createElement(RECT)
        element.setAttribute(SlimBase.x, x)
        element.setAttribute(SlimBase.y, y)
        element.setAttribute(SlimBase.width, width)
        element.setAttribute(SlimBase.height, height)
        return element
    }

    fun path(pathData: Any): SvgSlimShape {
        val element =
            createElement(PATH)
        element.setAttribute(SlimBase.pathData, pathData.toString())
        return element
    }
}
