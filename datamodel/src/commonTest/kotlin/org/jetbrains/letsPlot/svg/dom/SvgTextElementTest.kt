/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.svg.dom

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SvgTextElementTest {
    private val str = "Some text"
    private val altStr = "Some other text"

    @Test
    fun initEmpty() {
        val element = SvgTextElement()
        assertTrue(element.children().isEmpty())
    }

    @Test
    fun initString() {
        val element = SvgTextElement(str)
        assertSame(1, element.children().size)
        assertSame((element.children()[0] as SvgTextNode).textContent().get(), str)
    }

    @Test
    fun setText() {
        val element = SvgTextElement(str)
        element.setTextNode(altStr)
        assertSame(1, element.children().size)
        assertSame((element.children()[0] as SvgTextNode).textContent().get(), altStr)
    }

    @Test
    fun addText() {
        val element = SvgTextElement(str)
        element.addTextNode(altStr)
        assertSame(2, element.children().size)
        assertSame((element.children()[0] as SvgTextNode).textContent().get(), str)
        assertSame((element.children()[1] as SvgTextNode).textContent().get(), altStr)
    }
}