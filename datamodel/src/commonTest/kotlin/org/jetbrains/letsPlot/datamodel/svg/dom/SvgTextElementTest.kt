/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import kotlin.test.Test
import kotlin.test.assertEquals
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
        assertEquals(1, element.children().size)
        assertEquals((element.children()[0] as SvgTextNode).textContent().get(), str)
    }

    @Test
    fun setText() {
        val element = SvgTextElement(str)
        element.setTextNode(altStr)
        assertEquals(1, element.children().size)
        assertEquals((element.children()[0] as SvgTextNode).textContent().get(), altStr)
    }

    @Test
    fun addText() {
        val element = SvgTextElement(str)
        element.addTextNode(altStr)
        assertEquals(2, element.children().size)
        assertEquals((element.children()[0] as SvgTextNode).textContent().get(), str)
        assertEquals((element.children()[1] as SvgTextNode).textContent().get(), altStr)
    }
}