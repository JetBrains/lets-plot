/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

object Markdown {
    fun parse(text: String): Node {
        val tokens = Lexer.tokenize(text)
        val nodes = Parser.parse(tokens)

        return when (nodes.size) {
            0 -> Node.Group(emptyList())
            1 -> nodes[0]
            else -> Node.Group(nodes)
        }
    }
}

abstract class Node {
    data class Group(val children: List<Node>) : Node(){
        constructor(vararg children: Node) : this(children.toList())
    }
    data class Text(var text: String) : Node()
    data object Strong : Node()
    data object CloseStrong : Node()
    data object Em : Node()
    data object CloseEm : Node()
}
