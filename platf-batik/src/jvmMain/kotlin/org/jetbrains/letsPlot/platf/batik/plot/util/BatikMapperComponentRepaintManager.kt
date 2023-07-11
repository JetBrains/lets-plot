/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.batik.plot.util

import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.apache.batik.gvt.GraphicsNode
import java.awt.Component
import java.awt.geom.AffineTransform
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JComponent
import javax.swing.Timer
import kotlin.math.ceil

internal class BatikMapperComponentRepaintManager(
    private val component: Component,
    delay: Int   // ms
) {

    private val dirtyRects: AtomicReference<MutableSet<Rectangle>> = AtomicReference(LinkedHashSet())

    private val timer: Timer = Timer(delay) {
        val clipBoundsSet = dirtyRects.getAndSet(LinkedHashSet())
        repaintComponent(clipBoundsSet)
    }.apply { isRepeats = false }

    fun repaintNode(node: GraphicsNode) {

        if (!node.isVisible) return

        val bounds = node.getTransformedBounds(AffineTransform())
        if (bounds == null) return
        // Extend bounds a bit otherwise a small portion of tooltip sometimes remains after the tooltip is gone.
        val ext = 1
        val clipBounts = Rectangle(
            bounds.x.toInt() - ext,
            bounds.y.toInt() - ext,
            ceil(bounds.width).toInt() + 2 * ext,
            ceil(bounds.height).toInt() + 2 * ext
        )

        repaintBounds(clipBounts)
    }

    private fun repaintBounds(clipBounts: Rectangle) {
        if (clipBounts.isEmpty) return

        dirtyRects.get().add(clipBounts)

        if (!timer.isRunning) {
            timer.start()
        }
    }

    private fun repaintComponent(clipBoundsSet: Set<Rectangle>) {
        var rawBounds = clipBoundsSet.toList()
        val unitedBounds = ArrayList<Rectangle>()
        while (rawBounds.isNotEmpty()) {
            var r0 = rawBounds.first()
            if (rawBounds.size == 1) {
                rawBounds = emptyList()
            } else {
                rawBounds = rawBounds.subList(1, rawBounds.size).mapNotNull {
                    if (r0.intersects(it)) {
                        r0 = r0.union(it)
                        null
                    } else {
                        it
                    }
                }
            }
            unitedBounds.add(r0)
        }

//        println("repaint rects ${clipBoundsSet.size} -> ${unitedBounds.size}")
        unitedBounds.forEach {
//            if (it.dimension.length() > 200) {
//                println("Paint big: ${it.dimension}")
//            }

            (component as JComponent).paintImmediately(
                it.origin.x,
                it.origin.y,
                it.dimension.x,
                it.dimension.y,
            )
        }
    }

    fun stop() {
        timer.stop()
    }
}
