/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

object Markdown {
    fun parse(text: String): Node {
        val tokens = MdLexer.tokenize(text)
        val nodes = MdParser.parse(tokens)

        return when (nodes.size) {
            0 -> Node.Group(emptyList())
            1 -> nodes[0]
            else -> Node.Group(nodes)
        }
    }
}