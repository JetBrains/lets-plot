/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.livemap.ui.AttributionParts.SimpleLink
import jetbrains.livemap.ui.AttributionParts.SimpleText

interface AttributionParts {
    val text: String

    data class SimpleText(override val text: String): AttributionParts
    data class SimpleLink(val href: String, override val text: String) : AttributionParts
}

class LinkParser(private var xml: String) {
    private val regex = "(<a [^>]*>[^<]*<\\/a>|[^<]*)".toRegex()
    private val linkRegex = "href=\"([^\"]*)\"[^>]*>([^<]*)<\\/a>".toRegex()

    fun parse(): List<AttributionParts> {
        return ArrayList<AttributionParts>().apply {
            var result = regex.find(xml)

            while (result != null && result.value.isNotEmpty()) {
                val part = if (result.value.startsWith("<a")) {
                    parseLink(result.value)
                } else {
                    SimpleText(result.value)
                }

                add(part)
                result = result.next()
            }
        }
    }

    private fun parseLink(link: String): AttributionParts {
        val result = linkRegex.find(link)
        val (href, text) = result!!.destructured

        return SimpleLink(href, text)
    }
}