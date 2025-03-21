/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.repel

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleVectorExtensions.getXVector
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleVectorExtensions.getYVector
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle.Companion.savedNormalize
import kotlin.math.*
import kotlin.random.Random

/**
 * This label placement algorithm is based on:
 * Wanchun Li, Peter Eades, Nikola Nikolov —
 * "Using Spring Algorithms to Remove Node Overlapping"
 * Asia-Pacific Symposium on Information Visualisation (APVIS 2005),
 * CRPIT Volume 45, pp. 131–140.
 * https://crpit.scem.westernsydney.edu.au/confpapers/CRPITV45Li.pdf
 */
class LabelReplacer(
    boxes: Map<Int, TransformedRectangle>,
    circles: Map<Int, DoubleCircle>,
    hjust: Map<Int, Double>,
    vjust: Map<Int, Double>,
    boxPadding: Double,
    val bounds: DoubleRectangle,
    val maxOverlaps: Int = 10,
    val seed: Long? = null,
    val maxIter: Int = 2000,
    val direction: Direction = Direction.BOTH
) {
    private val labels = mutableListOf<LabelItem>()
    private val buddies = mutableListOf<LayoutItem>()
    private val rnd = if (seed == null) Random.Default else Random(seed)

    init {
        boxes.forEach { (dpIndex, box) ->
            val label = LabelItem(
                dpIndex,
                box,
                boxPadding,
                hjust[dpIndex] ?: 0.5,
                vjust[dpIndex] ?: 0.5,
                circles[dpIndex]!!.center,
                circles[dpIndex]!!.radius
            )

            labels.add(label)
            buddies.add(label)
        }

        circles.forEach { (dpIndex, circle) ->
            buddies.add(PointItem(dpIndex, circle))
        }
    }

    fun replace(): List<ReplaceResult> {
        val pauseIter = 0
        val firstRepulsionIter = 1
        val hideLineIter = 2
        for (iter in 0 until maxIter) {
            if (iter <= pauseIter) continue

            if (iter == firstRepulsionIter) {
                labels.forEach { label ->
                    val force = selfRepulsion(label)
                    label.update(force)
                    clampToBounds(label, bounds)
                }
                continue
            }

            var overlapsCount = 0

            for (label in labels) {
                if (label.hidden) continue

                if (iter % 10 == 0) {
                    // todo: add counter of intersection and use it to break main loop to avoid intersected segments on end
                    resolveSegmentIntersection(label)
                }

                var (force, overlaps) = aggregateForces(label)

                // hide overlapping label
                if (iter == hideLineIter && maxOverlaps >= 0 && overlaps > maxOverlaps) {
                    label.hidden = true
                    continue
                }

                if (overlaps == 0) {
                    force = selfAttraction(label)
                }

                label.update(force.mul(easeOutQuint(1 - iter.toDouble() / maxIter)))
                clampToBounds(label, bounds)
                // todo: try to add random force if boundary is reached

                overlapsCount += overlaps
            }

            if (overlapsCount == 0)
                break
        }

        val results = mutableListOf<ReplaceResult>()

        for (label in labels) {
            results.add(
                ReplaceResult(
                    label.dpIndex,
                    label.position,
                    label.hidden,
                )
            )
        }

        return results
    }

    private fun aggregateForces(buddy: LabelItem): Pair<DoubleVector, Int> {
        var force = DoubleVector.ZERO
        var overlaps = 0

        for (other in buddies) {
            if (buddy == other || other.hidden) continue

            if (buddy.intersects(other)) {
                overlaps++
                force = force.add(repulsion(buddy, other))
            }
        }

        return force to overlaps
    }

    private fun repulsion(buddy: LabelItem, other: LayoutItem): DoubleVector {
        val dir = normalizedNonZeroDirection(buddy.position, other.position)

        val dnl = buddy.dLength + other.dLength
        val d = distance(buddy, other)
        var forceValue = dnl / (dnl + d)

        if (buddy.dpIndex == other.dpIndex) {
            if (other is PointItem && other.circle.radius == 0.0) {
                return DoubleVector.ZERO
            }
            forceValue *= 2
        }

        return applyDirection(dir.negate().mul(forceValue))
    }

    private fun selfAttraction(label: LabelItem): DoubleVector {
        val dir = normalizedNonZeroDirection(label.position, label.point)
        val d = label.point.subtract(label.position).length()
        val dnl = label.dLength + label.pointRadius

        val forceValue = d / dnl

        return applyDirection(dir.mul(forceValue))
    }

    private fun selfRepulsion(label: LabelItem): DoubleVector {
        if (label.pointRadius == 0.0) {
            return DoubleVector.ZERO
        }

        val dir = normalizedNonZeroDirection(label.position, label.point)

        val forceValue = 8.0

        return applyDirection(dir.negate().mul(forceValue))
    }

    private fun applyDirection(force: DoubleVector): DoubleVector {
        return when (direction) {
            Direction.X -> force.getXVector()
            Direction.Y -> force.getYVector()
            Direction.BOTH -> force
        }
    }

    private fun clampToBounds(label: LabelItem, bounds: DoubleRectangle) {
        val bbox = label.box.bbox

        val dx = when {
            bbox.left < bounds.left -> bounds.left - bbox.left
            bbox.right > bounds.right -> bounds.right - bbox.right
            else -> 0.0
        }

        val dy = when {
            bbox.top < bounds.top -> bounds.top - bbox.top
            bbox.bottom > bounds.bottom -> bounds.bottom - bbox.bottom
            else -> 0.0
        }

        label.move(DoubleVector(dx, dy))
    }

    private fun resolveSegmentIntersection(label: LabelItem) {
        for (other in labels) {
            if (label == other || other.hidden) continue
            if (label.point == other.point)
                continue
            val labelSegment = DoubleSegment(label.point, label.position)
            val otherSegment = DoubleSegment(other.point, other.position)

            if (labelSegment.intersection(otherSegment) != null) {
                val delta = other.position.subtract(label.position)
                label.move(delta)
                other.move(delta.mul(-1.0))
            }
        }
    }

    fun distance(n1: LayoutItem, n2: LayoutItem): Double {
        return n1.position.subtract(n2.position).length()
    }

    private fun normalizedNonZeroDirection(from: DoubleVector, to: DoubleVector): DoubleVector {
        val dir = to.subtract(from).savedNormalize()
        return if (dir == DoubleVector.ZERO) randomVector() else dir
    }

    private fun randomVector(): DoubleVector {
        val angle = rnd.nextDouble() * 2 * PI
        return DoubleVector(cos(angle), sin(angle))
    }

    private fun easeOutQuint(x: Double): Double {
        return 1 - (1 - x).pow(5)
    }

    enum class Direction {
        BOTH, X, Y
    }

    interface LayoutItem {
        val dpIndex: Int
        val hypot: Double
        val position: DoubleVector
        var hidden: Boolean
        val dLength: Double
    }

    class LabelItem(
        override val dpIndex: Int,
        var box: TransformedRectangle,
        val padding: Double,
        val hjust: Double,
        val vjust: Double,
        val point: DoubleVector,
        val pointRadius: Double,
    ) : LayoutItem {
        override val hypot = box.hypot
        override var position = DoubleVector.ZERO
        override var hidden = false
        override val dLength: Double
            get() = box.hypot / 2
        val expanded: TransformedRectangle
            get() = box.expand(padding / 2)

        private var velocity = DoubleVector.ZERO
        private val friction = 0.7

        init {
            position = box.anchor(hjust, vjust)
        }

        fun update(force: DoubleVector) {
            velocity = velocity.mul(friction).add(force)
            move(velocity)
        }

        fun intersects(other: LayoutItem): Boolean {
            if (other is LabelItem) {
                return expanded.intersects(other.expanded)
            } else if (other is PointItem) {
                return box.intersects(other.circle)
            }
            return false
        }

        fun move(delta: DoubleVector) {
            position = position.add(delta)
            box = box.add(delta)
        }
    }

    class PointItem(
        override val dpIndex: Int,
        val circle: DoubleCircle
    ) : LayoutItem {
        override val hypot = 2 * circle.radius
        override val position = circle.center
        override var hidden = false
        override val dLength: Double
            get() = circle.radius
    }

    data class ReplaceResult(
        val index: Int,
        val point: DoubleVector,
        val hidden: Boolean
    )
}

