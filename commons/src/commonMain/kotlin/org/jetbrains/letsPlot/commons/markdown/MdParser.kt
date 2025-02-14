/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.MdLexer.Token
import org.jetbrains.letsPlot.commons.markdown.MdLexer.TokenType

// Reference: https://spec.commonmark.org/0.31.2/
internal class MdParser private constructor(
    private val tokens: List<Token>
) {
    class DelimiterInfo(
        val token: Token,
        val node: Node.Text,
        var count: Int,
        var active: Boolean = true,
        val opener: Boolean,
        val closer: Boolean
    ) {
        fun shrink(bool: Boolean) {
            val toDrop = (if (bool) 2 else 1).coerceAtMost(count)
            count -= toDrop
            node.text = node.text.dropLast(toDrop)
        }
    }

    private fun parse(): List<Node> {
        var i = 0
        val delimiters = mutableListOf<DelimiterInfo>()
        val nodes = mutableListOf<Node>()

        while (i < tokens.size) {
            val token = tokens[i]
            if (token.type == TokenType.ASTERISK || token.type == TokenType.UNDERSCORE) {
                if (tokens.getOrNull(i - 1)?.type == token.type) {
                    // Is not a run - previous token is a delimiter
                    nodes += Node.Text(token.value)
                    i++
                    continue
                }

                val opener = canOpenEmphasis(i)
                val closer = canCloseEmphasis(i)
                if (!opener && !closer) {
                    nodes += Node.Text(token.value)
                    i++
                } else {
                    val count = delimiterRunLength(tokens, i)
                    val text = Node.Text(tokens.subList(i, i + count).joinToString("") { it.value })
                    delimiters.add(DelimiterInfo(token, text, count, opener = opener, closer = closer))
                    nodes += text
                    i += count
                }
            } else {
                nodes += Node.Text(token.value)
                i++
            }
        }

        println(delimiters.joinToString("\n"))

        if (delimiters.size > 1) {
            processEmphasis(delimiters, nodes)
        }

        return nodes
    }

    private fun processEmphasis(
        infos: MutableList<DelimiterInfo>,
        nodes: MutableList<Node>,
        stackBottom: Int? = null
    ) {
        var closer: DelimiterInfo? = null
        var currentPosition = if (stackBottom == null) infos.lastIndex else stackBottom + 1

        val openersBottom = mapOf(
            TokenType.ASTERISK to (stackBottom ?: (0)),
            TokenType.UNDERSCORE to (stackBottom ?: (0))
        )

       while(true) {
            closer = null
            while (currentPosition < infos.size) {
                val currentDelimiter = infos[currentPosition]
                if (currentDelimiter.closer) {
                    closer = currentDelimiter
                    break
                }
                currentPosition++
            }

            if (closer == null) {
                return
            }

            var lookBack = currentPosition - 1

            var opener: DelimiterInfo? = null

            while (lookBack >= (stackBottom ?: 0) && lookBack >= openersBottom[closer.token.type]!!) {
                if (infos[lookBack].opener) {
                    opener = infos[lookBack]
                    break
                }

                lookBack--
            }

            if (opener != null) {
                val strong = opener.count >= 2 && closer.count >= 2
                nodes.add(nodes.indexOfFirst { it === opener.node } + 1, if (strong) Node.Strong("") else Node.Emph(""))
                nodes.add(nodes.indexOfFirst { it === closer.node }, if (strong) Node.CloseStrong("") else Node.CloseEmph(""))
                val toRemoveStart = infos.indexOf(opener) + 1
                val toRemoveEnd = infos.indexOf(closer) - 1

                if (toRemoveEnd - toRemoveStart > 0) {
                    infos.subList(toRemoveStart, toRemoveEnd).clear()
                }

                opener.shrink(strong)
                closer.shrink(strong)

                if (opener.count == 0) {
                    infos.remove(opener)
                    nodes.remove(opener.node)
                }

                if (closer.count == 0) {
                    infos.remove(closer)
                    nodes.remove(closer.node)
                    currentPosition--
                }
            }
        }
    }

    private fun parseOld(): List<Node> {
        return parse(0..tokens.size, false, false)
    }

    private fun parse(range: IntRange, isBold: Boolean, isItalic: Boolean): List<Node> {
        val buffer = StringBuilder()

        fun buildText(): Node {
            val text = buffer.toString()
            buffer.clear()
            return when {
                isBold && isItalic -> Node.BoldItalic(text)
                isBold -> Node.Strong(text)
                isItalic -> Node.Emph(text)
                else -> Node.Text(text)
            }
        }

        var i = range.start
        val nodes = mutableListOf<Node>()

        while (i < range.endInclusive) {
            val token = tokens[i]
            when (token.type) {
                TokenType.TEXT -> buffer.append(token.value).also { i++ }
                TokenType.WHITE_SPACE -> buffer.append(token.value).also { i++ }
                TokenType.ASTERISK -> {
                    val runLength = delimiterRunLength(tokens, i)
                    val nextDelimiterRun = findNextDelimiterRun(tokens, i + runLength, TokenType.ASTERISK)

                    if (canOpenEmphasis(i) && canCloseEmphasis(nextDelimiterRun)) {

                        if (buffer.isNotEmpty()) {
                            nodes += buildText()
                        }

                        val innerRange = (i + runLength) .. nextDelimiterRun
                        val innerNode = parse(innerRange, runLength == 2 || runLength >= 3, runLength == 1 || runLength >= 3)
                        nodes.addAll(innerNode)
                        i = nextDelimiterRun + runLength
                    } else {
                        buffer.append(token.value)
                        i++
                    }
                }

                TokenType.UNDERSCORE -> {
                    val runLength = delimiterRunLength(tokens, i)
                    val nextDelimiterRun = findNextDelimiterRun(tokens, i + runLength, TokenType.UNDERSCORE)

                    if (canOpenEmphasis(i) && canCloseEmphasis(nextDelimiterRun)) {

                        if (buffer.isNotEmpty()) {
                            nodes += buildText()
                        }

                        val innerRange = (i + runLength) .. nextDelimiterRun
                        val innerNode = parse(innerRange, runLength == 2 || runLength >= 3, runLength == 1 || runLength >= 3)
                        nodes.addAll(innerNode)
                        i = nextDelimiterRun + runLength
                    } else {
                        buffer.append(token.value)
                        i++
                    }
                }

                TokenType.BACKSLASH -> {
                    buffer.append(tokens[i + 1].value)
                    i += 2
                }

                else -> error("Unexpected token: $token")
            }
        }

        if (buffer.isNotEmpty()) {
            nodes += buildText()
        }

        return nodes
    }

    // https://spec.commonmark.org/0.31.2/#delimiter-run
    private fun findNextDelimiterRun(tokens: List<Token>, index: Int, token: TokenType): Int {
        var cur = index
        while (cur < tokens.size) {
            if (tokens[cur].type == token) {
                return cur
            }
            cur++
        }
        return -1
    }

    // https://spec.commonmark.org/0.31.2/#delimiter-run
    private fun delimiterRunLength(tokens: List<Token>, index: Int): Int {
        val token = tokens[index]
        val nextToken = tokens.getOrNull(index + 1)
        val nextNextToken = tokens.getOrNull(index + 2)

        return when {
            token.type == TokenType.ASTERISK && nextToken?.type == TokenType.ASTERISK && nextNextToken?.type == TokenType.ASTERISK -> 3
            token.type == TokenType.UNDERSCORE && nextToken?.type == TokenType.UNDERSCORE && nextNextToken?.type == TokenType.UNDERSCORE -> 3
            token.type == TokenType.ASTERISK && nextToken?.type == TokenType.ASTERISK -> 2
            token.type == TokenType.UNDERSCORE && nextToken?.type == TokenType.UNDERSCORE -> 2
            else -> 1
        }
    }

    // https://spec.commonmark.org/0.31.2/#can-open-emphasis
    private fun canOpenEmphasis(index: Int): Boolean {
        val token = tokens[index].type

        val leftFlankingDelimiterRun = isLeftFlankingDelimiterRun(tokens, index)
        
        if (token == TokenType.ASTERISK) {
            return leftFlankingDelimiterRun
        }

        if (token == TokenType.UNDERSCORE) {
            val rightFlankingDelimiterRun = false//isRightFlankingDelimiterRun(tokens, index)
            val precededByPunctuation = tokens.getOrNull(index - 1)?.type == TokenType.PUNCTUATION
            
            val res = leftFlankingDelimiterRun
                    && (!rightFlankingDelimiterRun || (rightFlankingDelimiterRun && precededByPunctuation))

            return res
        }

        return false
    }

    // https://spec.commonmark.org/0.31.2/#can-close-emphasis
    private fun canCloseEmphasis(index: Int): Boolean {
        val token = tokens[index].type

        val rightFlankingDelimiterRun = isRightFlankingDelimiterRun(tokens, index)
        
        if (token == TokenType.ASTERISK) {
            return rightFlankingDelimiterRun
        }

        if (token == TokenType.UNDERSCORE) {
            val leftFlankingDelimiterRun = isLeftFlankingDelimiterRun(tokens, index)
            val length = delimiterRunLength(tokens, index)

            val followedByPunctuation = tokens.getOrNull(index + length)?.type == TokenType.PUNCTUATION
            val res = rightFlankingDelimiterRun &&
                    (!leftFlankingDelimiterRun || (leftFlankingDelimiterRun && followedByPunctuation))

            return res
        }

        return false
    }

    // https://spec.commonmark.org/0.31.2/#left-flanking-delimiter-run
    private fun isLeftFlankingDelimiterRun(tokens: List<Token>, index: Int): Boolean {
        val runLength = delimiterRunLength(tokens, index)

        val nextTokenType = tokens.getOrNull(index + runLength)?.type ?: TokenType.WHITE_SPACE
        if (nextTokenType == TokenType.WHITE_SPACE) {
            return false
        }

        if (nextTokenType != TokenType.PUNCTUATION) {
            return true
        }

        if (nextTokenType == TokenType.PUNCTUATION) {
            val prevTokenType = tokens.getOrNull(index - 1)?.type ?: TokenType.WHITE_SPACE
            if (prevTokenType == TokenType.WHITE_SPACE || prevTokenType == TokenType.PUNCTUATION) {
                return true
            }
        }

        return false
    }

    // https://spec.commonmark.org/0.31.2/#right-flanking-delimiter-run
    private fun isRightFlankingDelimiterRun(tokens: List<Token>, index: Int): Boolean {
        val runLength = delimiterRunLength(tokens, index)

        val prevTokenType = tokens.getOrNull(index - 1)?.type ?: TokenType.WHITE_SPACE
        if (prevTokenType == TokenType.WHITE_SPACE) {
            return false
        }

        if (prevTokenType != TokenType.PUNCTUATION) {
            return true
        }

        if (prevTokenType == TokenType.PUNCTUATION) {
            val nextTokenType = tokens.getOrNull(index + runLength)?.type ?: TokenType.WHITE_SPACE
            if (nextTokenType == TokenType.WHITE_SPACE || nextTokenType == TokenType.PUNCTUATION) {
                return true
            }
        }

        return false
    }

    companion object {
        fun parse(tokens: List<Token>): List<Node> {
            return MdParser(tokens).parse()
        }
    }


}


