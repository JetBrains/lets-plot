/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal fun union(rects: List<DoubleRectangle>): DoubleRectangle? =
    rects.fold<DoubleRectangle, DoubleRectangle?>(null) { acc, rect ->
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

internal fun breadthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(element: Element): Sequence<Element> {
        return when (element) {
            is Container -> element.children.asSequence() + element.children.asSequence().flatMap(::enumerate)
            else -> emptySequence()
        }
    }

    return sequenceOf(element) + enumerate(element)
}

internal fun reversedBreadthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(element: Element): Sequence<Element> {
        return when (element) {
            is Container -> {
                val reversed = element.children.asReversed().asSequence()
                reversed.flatMap(::enumerate) + reversed
            }

            else -> emptySequence()
        }
    }

    return enumerate(element) + sequenceOf(element)
}

internal fun depthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(el: Element): Sequence<Element> {
        return when (el) {
            is Container -> sequenceOf(el) + el.children.asSequence().flatMap(::enumerate)
            else -> sequenceOf(el)
        }
    }

    return enumerate(element)
}

internal fun reversedDepthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(el: Element): Sequence<Element> {
        return when (el) {
            is Container -> el.children.asReversed().asSequence().flatMap(::enumerate) + sequenceOf(el)
            else -> sequenceOf(el)
        }
    }

    return enumerate(element)
}

fun Color.changeAlpha(a: Float) = changeAlpha((255 * a).roundToInt())
