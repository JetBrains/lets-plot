/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.ui

import org.jetbrains.letsPlot.livemap.core.graphics.Attribution.AttributionParser
import org.jetbrains.letsPlot.livemap.core.graphics.Attribution.AttributionParts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttributionParserTest {

    @Test
    fun extraParenthesis() {
        val string = "<<a href=\"one\">two</a>"
        val parts: List<AttributionParts> = AttributionParser.parse(string)

        assertEquals(AttributionParts.SimpleLink("one", "two" ), parts[0])
    }

    @Test
    fun mistakeInLink() {
        val string = "One <a hef=\"two\">Three</a> Four"
        val parts: List<AttributionParts> = AttributionParser.parse(string)
        assertEquals(AttributionParts.SimpleText("<a hef=\"two\">Three</a>"), parts[1])
    }

    @Test
    fun forgottenHref() {
        val string = "One <a>Two</a> Three"

        val parts: List<AttributionParts> = AttributionParser.parse(string)
        assertEquals(AttributionParts.SimpleText("<a>Two</a>"), parts[1])
    }

    @Test
    fun emptyHref() {
        val string = "One <a href=\"\">Two</a> Three"
        val parts: List<AttributionParts> = AttributionParser.parse(string)
        assertEquals(AttributionParts.SimpleText("<a href=\"\">Two</a>"), parts[1])
    }

    @Test
    fun complexTest() {
        val string = "Map tiles by <a href=\"http://stamen.com\">Stamen Design</a>, under <a href=\"http://creativecommons.org/licenses/by/3.0\">CC BY 3.0</a>. Data by <a href=\"http://openstreetmap.org\">OpenStreetMap</a>, under <a href=\"http://www.openstreetmap.org/copyright\">ODbL</a>"

        val parts: List<AttributionParts> = AttributionParser.parse(string)
        assertTrue { parts.size == 8 }
    }

    @Test
    fun emptyString() {
        val string = ""
        val parts: List<AttributionParts> = AttributionParser.parse(string)
        assertTrue { parts.isEmpty() }
    }

    @Test
    fun onlyText() {
        val string = "OpenStreetMap"
        val parts: List<AttributionParts> = AttributionParser.parse(string)

        assertEquals(AttributionParts.SimpleText(string), parts[0])
    }

    @Test
    fun onlyLink() {
        val string = "<a href=\"http://stamen.com\">Stamen Design</a>"
        val parts: List<AttributionParts> = AttributionParser.parse(string)

        assertEquals(AttributionParts.SimpleLink("http://stamen.com", "Stamen Design" ), parts[0])
    }

    @Test
    fun twoLink() {
        val string = "<a href=\"http://stamen.com\">Stamen Design</a><a href=\"http://stamen.com\">Stamen Design</a>"
        val parts: List<AttributionParts> = AttributionParser.parse(string)

        assertEquals(AttributionParts.SimpleLink("http://stamen.com", "Stamen Design" ), parts[0])
        assertEquals(AttributionParts.SimpleLink("http://stamen.com", "Stamen Design" ), parts[1])
    }

    @Test
    fun linkWithoutText() {
        val string = "<a href=\"http://stamen.com\"></a>"
        val parts: List<AttributionParts> = AttributionParser.parse(string)

        assertEquals(AttributionParts.SimpleLink("http://stamen.com", ""), parts[0])
    }
}