/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.repel

import org.jetbrains.letsPlot.commons.SystemTime
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
class LabelForceLayout(
    boxes: Map<Int, TransformedRectangle>,
    circles: Map<Int, DoubleCircle>,
    hjust: Map<Int, Double>,
    vjust: Map<Int, Double>,
    boxPadding: Double,
    val bounds: DoubleRectangle,
    val maxOverlaps: Int,
    val seed: Long?,
    val maxIter: Int,
    val maxTime: Double,
    val direction: Direction
) {
    private val systemTime = SystemTime()
    private val labelItems = mutableListOf<LabelItem>()
    private val layoutItems = mutableListOf<LayoutItem>()
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

            labelItems.add(label)
            layoutItems.add(label)
        }

        circles.forEach { (dpIndex, circle) ->
            if (circle.radius > 0.0) {
                layoutItems.add(PointItem(dpIndex, circle))
            }
        }
    }

    fun doLayout(): List<LabelItem> {
        val start = systemTime.getTimeMs()
        val pauseIter = 0
        val firstRepulsionIter = 1
        val hideLineIter = 2
        for (iter in 0 until maxIter) {
            if (iter <= pauseIter) continue

            if (iter == firstRepulsionIter) {
                labelItems.forEach { label ->
                    val force = selfRepulsion(label)
                    label.setForce(force)
                    clampToBounds(label, bounds)
                }
                continue
            }

            val easeFactor = easeOutQuint(1 - iter.toDouble() / maxIter)
            var overlapsCount = 0

            for (label in labelItems) {
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

                label.setForce(force.mul(easeFactor))
                clampToBounds(label, bounds)
                // todo: try to add random force if boundary is reached

                overlapsCount += overlaps
            }

            if (overlapsCount == 0)
                break

            if (maxTime > 0 && systemTime.getTimeMs() - start > maxTime) {
                break
            }
        }

        return labelItems
    }

    private fun aggregateForces(labelItem: LabelItem): Pair<DoubleVector, Int> {
        var force = DoubleVector.ZERO
        var overlaps = 0

        for (other in layoutItems) {
            if (labelItem == other || other.hidden) continue

            if (labelItem.intersects(other)) {
                overlaps++
                force = force.add(repulsion(labelItem, other))
            } else {
                if (other is LabelItem) {
                    val segment = other.segment() ?: continue
                    if (labelItem.box.intersects(segment)) {
                        val delta = perpendicularVectorFromSegment(labelItem.position, segment).savedNormalize()
                        force = force.add(applyDirection(delta))
                        overlaps++
                    }
                }
            }
        }

        return force to overlaps
    }

    private fun repulsion(labelItem: LabelItem, otherItem: LayoutItem): DoubleVector {
        val dir = normalizedNonZeroDirection(labelItem.position, otherItem.position)

        val dnl = labelItem.dLength + otherItem.dLength
        val d = distance(labelItem, otherItem)
        var forceValue = dnl / (dnl + d)

        if (labelItem.dpIndex == otherItem.dpIndex) {
            forceValue *= 2
        }

        return applyDirection(dir.negate().mul(forceValue))
    }

    private fun selfAttraction(labelItem: LabelItem): DoubleVector {
        val dir = normalizedNonZeroDirection(labelItem.position, labelItem.point)
        val d = labelItem.point.subtract(labelItem.position).length()
        val dnl = labelItem.dLength + labelItem.pointRadius

        val forceValue = d / dnl

        return applyDirection(dir.mul(forceValue))
    }

    private fun selfRepulsion(labelItem: LabelItem): DoubleVector {
        if (labelItem.pointRadius == 0.0) {
            return DoubleVector.ZERO
        }

        val dir = normalizedNonZeroDirection(labelItem.position, labelItem.point)

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

    private fun clampToBounds(labelItem: LabelItem, bounds: DoubleRectangle) {
        val bbox = labelItem.box.bbox

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

        labelItem.updatePosition(DoubleVector(dx, dy))
    }

    private fun resolveSegmentIntersection(labelItem: LabelItem) {
        for (otherItem in labelItems) {
            if (labelItem == otherItem || otherItem.hidden) continue
            if (labelItem.point == otherItem.point)
                continue
            val labelSegment = DoubleSegment(labelItem.point, labelItem.position)
            val otherSegment = DoubleSegment(otherItem.point, otherItem.position)

            if (labelSegment.intersection(otherSegment) != null) {
                val delta = otherItem.position.subtract(labelItem.position)
                labelItem.updatePosition(delta)
                otherItem.updatePosition(delta.mul(-1.0))
            }
        }
    }

    fun perpendicularVectorFromSegment(p: DoubleVector, segment: DoubleSegment): DoubleVector {
        val ab = segment.end.subtract(segment.start)
        val ap = p.subtract(segment.start)

        val abLengthSquared = ab.dotProduct(ab)
        if (abLengthSquared == 0.0) {
            return segment.start.subtract(p)
        }

        val t = (ap.dotProduct(ab) / abLengthSquared).coerceIn(0.0, 1.0)
        val projection = segment.start.add(ab.mul(t))
        return p.subtract(projection)
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

        fun setForce(force: DoubleVector) {
            velocity = velocity.mul(friction).add(force)
            updatePosition(velocity)
        }

        fun segment(): DoubleSegment? {
            return box.shortestSegmentToRectangleEdgeCenter(point)
        }

        fun intersects(other: LayoutItem): Boolean {
            if (other is LabelItem) {
                return expanded.intersects(other.expanded)
            } else if (other is PointItem) {
                return box.intersects(other.circle)
            }
            return false
        }

        fun updatePosition(delta: DoubleVector) {
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
}

