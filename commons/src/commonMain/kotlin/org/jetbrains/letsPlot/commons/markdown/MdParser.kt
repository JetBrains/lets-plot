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

        override fun toString(): String {
            return "DelimiterInfo(token=$token, node=$node, count=$count, active=$active, opener=$opener, closer=$closer)"
        }
    }

    private fun parse(): List<Node> {
        var i = 0
        val delimiters = mutableListOf<DelimiterInfo>()
        val nodes = mutableListOf<Node>()

        while (i < tokens.size) {
            val token = tokens[i]
            if (token.type == TokenType.BACKSLASH) {
                nodes += Node.Text(tokens[i + 1].value)
                i += 2
            } else
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

        if (delimiters.size > 1) {
            processEmphasis(delimiters, nodes)
        }

        return joinTextNodes(nodes)
    }

    private fun joinTextNodes(nodes: List<Node>): MutableList<Node> {
        val res = mutableListOf<Node>()
        var i = 0
        while (i < nodes.size) {
            if (nodes[i] is Node.Text) {
                val start = i
                while (i < nodes.size && nodes[i] is Node.Text) {
                    i++
                }
                val end = i
                val text = nodes.subList(start, end).joinToString("") { (it as Node.Text).text }

                res.add(Node.Text(text))
            } else {
                res.add(nodes[i])
                i++
            }
        }

        return res
    }

    // Reference:
    // https://github.com/commonmark/cmark/blob/3460cd809b6dd311b58e92733ece2fc956224fd2/src/inlines.c#L651
    private fun processEmphasis(infos: MutableList<DelimiterInfo>, nodes: MutableList<Node>, stackBottom: Int = 0) {
        if (infos.isEmpty()) {
            return
        }

        var currentPosition = if (stackBottom == null) 0 else stackBottom + 1
        var closer: DelimiterInfo? = null

        val openersBottom = mutableMapOf<Int, Int>()

        while (true) {
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

            var openerPosition = currentPosition - 1

            var opener: DelimiterInfo? = infos.getOrNull(openerPosition)
            var openerFound = false

            val openerBottomIndex = toOpenerIndex(closer)

            while (opener != null && openerPosition >= (openersBottom[openerBottomIndex] ?: stackBottom)) {
                if (opener.opener && opener.token.type == closer.token.type) {
                    if (!(closer.opener || opener.closer)
                        || closer.count % 3 == 0
                        || (opener.count + closer.count) % 3 != 0
                    ) {
                        openerFound = true
                        break
                    }
                }

                openerPosition--
                opener = infos.getOrNull(openerPosition)
            }

            val oldCloser = closer

            if (openerFound) {
                val strong = opener!!.count >= 2 && closer.count >= 2
                nodes.add(nodes.indexOfFirst { it === opener.node } + 1, if (strong) Node.Strong else Node.Emph)
                nodes.add(nodes.indexOfFirst { it === closer.node }, if (strong) Node.CloseStrong else Node.CloseEmph)
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
                }
                currentPosition--
            } else {
                currentPosition++
                //infos.remove(closer)
                openersBottom[toOpenerIndex(closer)] = currentPosition
            }
        }
    }

    // https://spec.commonmark.org/0.31.2/#delimiter-run
    private fun delimiterRunLength(tokens: List<Token>, index: Int): Int {
        val token = tokens[index]

        var i = index + 1
        while (i < tokens.size && tokens[i].type == token.type) {
            i++
        }

        return i - index
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

    private fun toOpenerIndex(delimiter: DelimiterInfo): Int {
        val type = delimiter.token.type.ordinal
        val canOpen = 10 + if (delimiter.opener) 1 else 0
        val mod3 = delimiter.count % 3

        return type + (canOpen * 10) + (mod3 * 100)
    }


    companion object {
        fun parse(tokens: List<Token>): List<Node> {
            return MdParser(tokens).parse()
        }
    }
}


