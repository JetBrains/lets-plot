/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

internal class DelimiterRun(
    val tokenType: MdLexer.TokenType,
    val node: Node.Text,
    var count: Int,
    var active: Boolean = true,
    val canOpen: Boolean,
    val canClose: Boolean
) {
    fun shrink(bool: Boolean) {
        val toDrop = (if (bool) 2 else 1).coerceAtMost(count)
        count -= toDrop
        node.text = node.text.dropLast(toDrop)
    }

    override fun toString(): String {
        return "DelimiterInfo(tokenType=$tokenType, node=$node, count=$count, active=$active, opener=$canOpen, closer=$canClose)"
    }
}
