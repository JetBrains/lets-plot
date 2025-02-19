/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

object Markdown {
    fun parse(text: String): List<Node> {
        val tokens = Lexer.tokenize(text)
        return Parser.parse(tokens)
    }
}

abstract class Node {
    data class Text(var text: String) : Node()
    data object Strong : Node()
    data object CloseStrong : Node()
    data object Em : Node()
    data object CloseEm : Node()
}
