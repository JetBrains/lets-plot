/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.MdLexer.Token
import org.jetbrains.letsPlot.commons.markdown.MdLexer.TokenType
import org.jetbrains.letsPlot.commons.markdown.Node.*

// Reference: https://spec.commonmark.org/0.31.2/
internal class MdParser private constructor(
    private val tokens: List<Token>
) {

    private fun parse(): List<Node> {
        var i = 0
        val delimiters = mutableListOf<DelimiterRun>()
        val nodes = mutableListOf<Node>()

        while (i < tokens.size) {
            val token = tokens[i]
            when (token.type) {
                TokenType.BACKSLASH -> {
                    val nextToken = tokens.getOrNull(i + 1)
                    nodes += when (nextToken) {
                        null -> Text(token.value).also { i++ }
                        else -> when (nextToken.type) {
                            TokenType.ASTERISK,
                            TokenType.UNDERSCORE,
                            TokenType.BACKSLASH -> Text(nextToken.value).also { i += 2 }

                            else -> Text(token.value).also { i++ } // output backslash as is
                        }
                    }
                }

                TokenType.ASTERISK, TokenType.UNDERSCORE -> {
                    val count = delimiterRunLength(tokens, i)
                    val canOpen = canOpenEmphasis(i)
                    val canClose = canCloseEmphasis(i)

                    val text = Text(token.value.repeat(count))
                    nodes += text

                    if (canOpen || canClose) {
                        delimiters += DelimiterRun(token.type, text, count, canOpen = canOpen, canClose = canClose)
                    }
                    i += count
                }

                else -> {
                    nodes += Text(token.value)
                    i++
                }
            }
        }

        if (delimiters.size > 1) {
            processEmphasis(delimiters, nodes)
        }

        return joinTextNodes(nodes)
    }

    private fun joinTextNodes(nodes: MutableList<Node>): MutableList<Node> {
        var currentTextNode: Text? = null
        var i = 0

        while (i < nodes.size) {
            when (val node = nodes[i] as? Text) {
                null -> currentTextNode = null.also { i++ }
                else -> when (currentTextNode) {
                    null -> currentTextNode = node.also { i++ }
                    else -> currentTextNode.text += node.text.also { nodes.removeAt(i) }
                }
            }
        }

        return nodes
    }

    // Reference:
    // https://github.com/commonmark/cmark/blob/3460cd809b6dd311b58e92733ece2fc956224fd2/src/inlines.c#L651
    private fun processEmphasis(infos: MutableList<DelimiterRun>, nodes: MutableList<Node>, stackBottom: Int = 0) {
        if (infos.isEmpty()) {
            return
        }

        val openersBottom = mutableMapOf<Int, Int>()

        var currentPosition = stackBottom
        var closer: DelimiterRun? = infos.getOrNull(currentPosition)

        while (closer != null) {
            if (closer.canClose) {

                val openerBottomIndex = closer.tokenType.ordinal * 10 +
                        (if (closer.canOpen) 3 else 0) +
                        closer.count % 3

                var openerPosition = currentPosition - 1
                var opener = infos.getOrNull(openerPosition)
                var openerFound = false

                while (opener != null && openerPosition >= (openersBottom[openerBottomIndex] ?: stackBottom)) {
                    if (opener.canOpen && opener.tokenType == closer.tokenType) {
                        if (!(closer.canOpen || opener.canClose)
                            || closer.count % 3 == 0
                            || (opener.count + closer.count) % 3 != 0
                        ) {
                            openerFound = true
                            break
                        }
                    }
                    opener = infos.getOrNull(--openerPosition)
                }

                if (openerFound) {
                    val strong = opener!!.count >= 2 && closer.count >= 2
                    nodes.add(nodes.indexOfFirst { it === opener.node } + 1, if (strong) Strong else Emph)
                    nodes.add(nodes.indexOfFirst { it === closer!!.node }, if (strong) CloseStrong else CloseEmph)

                    opener.shrink(strong)
                    closer.shrink(strong)
                    if (opener.count == 0) {
                        infos.remove(opener)
                        nodes.remove(opener.node)
                    }
                    if (closer.count == 0) {
                        infos.remove(closer)
                        nodes.remove(closer.node)
                    }

                    closer = infos.getOrNull(--currentPosition)
                } else {
                    openersBottom[openerBottomIndex] = currentPosition
                    if (!closer.canOpen) {
                        //nodes.remove(closer.node)
                    }
                    closer = infos.getOrNull(++currentPosition)
                }
            } else {
                closer = infos.getOrNull(++currentPosition)
            }
        }
    }

    // https://spec.commonmark.org/0.31.2/#delimiter-run
    private fun delimiterRunLength(tokens: List<Token>, index: Int): Int {
        val type = tokens[index].type
        return tokens.asSequence().drop(index).takeWhile { it.type == type }.count()
    }

    // https://spec.commonmark.org/0.31.2/#can-open-emphasis
    private fun canOpenEmphasis(index: Int): Boolean {
        val token = tokens[index].type

        val leftFlankingDelimiterRun = isLeftFlankingDelimiterRun(tokens, index)

        if (token == TokenType.ASTERISK) {
            return leftFlankingDelimiterRun
        }

        if (token == TokenType.UNDERSCORE) {
            val rightFlankingDelimiterRun = isRightFlankingDelimiterRun(tokens, index)
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


