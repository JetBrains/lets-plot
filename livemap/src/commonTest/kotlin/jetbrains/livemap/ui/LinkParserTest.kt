/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinkParserTest {

    @Test
    fun complexTest() {
        val string = "Map tiles by <a href=\"http://stamen.com\">Stamen Design</a>, under <a href=\"http://creativecommons.org/licenses/by/3.0\">CC BY 3.0</a>. Data by <a href=\"http://openstreetmap.org\">OpenStreetMap</a>, under <a href=\"http://www.openstreetmap.org/copyright\">ODbL</a>"

        val parts: List<AttributionParts> = LinkParser(string).parse()
        assertTrue { parts.size == 8 }
    }

    @Test
    fun emptyString() {
        val string = ""
        val parts: List<AttributionParts> = LinkParser(string).parse()
        assertTrue { parts.isEmpty() }
    }

    @Test
    fun onlyText() {
        val string = "OpenStreetMap"
        val parts: List<AttributionParts> = LinkParser(string).parse()

        assertEquals(AttributionParts.SimpleText(string), parts[0])
    }

    @Test
    fun onlyLink() {
        val string = "<a href=\"http://stamen.com\">Stamen Design</a>"
        val parts: List<AttributionParts> = LinkParser(string).parse()

        assertEquals(AttributionParts.SimpleLink("http://stamen.com", "Stamen Design" ), parts[0])
    }

    @Test
    fun twoLink() {
        val string = "<a href=\"http://stamen.com\">Stamen Design</a><a href=\"http://stamen.com\">Stamen Design</a>"
        val parts: List<AttributionParts> = LinkParser(string).parse()

        assertEquals(AttributionParts.SimpleLink("http://stamen.com", "Stamen Design" ), parts[0])
        assertEquals(AttributionParts.SimpleLink("http://stamen.com", "Stamen Design" ), parts[1])
    }

    @Test
    fun linkWithoutText() {
        val string = "<a href=\"http://stamen.com\"></a>"
        val parts: List<AttributionParts> = LinkParser(string).parse()

        assertEquals(AttributionParts.SimpleLink("http://stamen.com", ""), parts[0])
    }
}