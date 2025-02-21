/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

object Markdown {
    fun mdToHtml(text: String): String {
        val tokens = Lexer.tokenize(text)
        return Parser.parse(tokens)
    }
}
