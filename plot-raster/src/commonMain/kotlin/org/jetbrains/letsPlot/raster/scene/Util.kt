/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal fun union(rects: List<DoubleRectangle>): DoubleRectangle? =
    rects.fold(null) { acc, rect ->
        if (acc != null) {
            DoubleRectangle.LTRB(
                min(rect.left, acc.left),
                min(rect.top, acc.top),
                max(rect.right, acc.right),
                max(rect.bottom, acc.bottom)
            )
        } else {
            rect
        }
    }

internal fun breadthFirstTraversal(node: Node): Sequence<Node> {
    fun enumerate(node: Node): Sequence<Node> {
        return when (node) {
            is Container -> node.children.asSequence() + node.children.asSequence().flatMap(::enumerate)
            else -> emptySequence()
        }
    }

    return sequenceOf(node) + enumerate(node)
}

internal fun reversedBreadthFirstTraversal(node: Node): Sequence<Node> {
    fun enumerate(node: Node): Sequence<Node> {
        return when (node) {
            is Container -> {
                val reversed = node.children.asReversed().asSequence()
                reversed.flatMap(::enumerate) + reversed
            }

            else -> emptySequence()
        }
    }

    return enumerate(node) + sequenceOf(node)
}

internal fun depthFirstTraversal(node: Node, onlyVisible: Boolean = false): Sequence<Node> {
    fun enumerate(el: Node): Sequence<Node> {
        if (onlyVisible && !el.isVisible) {
            return emptySequence()
        }

        return when (el) {
            is Container -> sequenceOf(el) + el.children.asSequence().flatMap(::enumerate)
            else -> sequenceOf(el)
        }
    }

    return enumerate(node)
}

internal fun reversedDepthFirstTraversal(node: Node): Sequence<Node> {
    fun enumerate(el: Node): Sequence<Node> {
        return when (el) {
            is Container -> el.children.asReversed().asSequence().flatMap(::enumerate) + sequenceOf(el)
            else -> sequenceOf(el)
        }
    }

    return enumerate(node)
}

fun Color.changeAlpha(a: Float) = changeAlpha((255 * a).roundToInt())
