/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.annotations

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class Annotations(
    private val lines: List<LineSpec>,
    val textStyle: TextStyle
) {
    private val myFormatters: MutableMap<Aes<*>, (Any?) -> String> = HashMap()
    private var myFormatterCreator: ((Aes<*>) -> ((Any?) -> String))? = null

    fun initFormatter(creator:(Aes<*>) -> ((Any?) -> String)): Annotations {
        myFormatterCreator = creator
        return this
    }

    private fun getFormatter(aes: Aes<*>): (Any?) -> String {
        return myFormatters.getOrPut(aes) {
            myFormatterCreator?.let { creator -> creator(aes) } ?: Any?::toString
        }
    }

    fun getAnnotationText(index: Int): String {
        return lines.mapNotNull { it.getAnnotationText(index, ::getFormatter) }.joinToString("\n")
    }
}