/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml

internal data class Token(
    val type: TokenType,
    val value: String
) {
    companion object {
        val LT = Token(TokenType.LT, "<")
        val LT_SLASH = Token(TokenType.LT_SLASH, "</")
        val GT = Token(TokenType.GT, ">")
        val SLASH = Token(TokenType.SLASH, "/")
        val SLASH_GT = Token(TokenType.SLASH_GT, "/>")
        val EQUALS = Token(TokenType.EQUALS, "=")
        val EOF = Token(TokenType.EOF, "")
    }
}