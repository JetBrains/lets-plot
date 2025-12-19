package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.math.ceil
import org.jetbrains.letsPlot.commons.intern.math.floor
import org.jetbrains.letsPlot.commons.intern.math.subtract
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.raster.shape.Element

internal class RepaintManager(
    private val canvasPeer: CanvasPeer
) : Disposable {
    private val elementCache = mutableMapOf<Element, CacheEntry>()

    fun containsElement(element: Element): Boolean {
        return elementCache.containsKey(element)
    }

    fun cacheElement(
        element: Element,
        viewportSize: Vector,
        contentScale: Double,
        painter: (Context2d) -> Unit
    ): Boolean {
        val inverseCtm = element.ctm.inverse() ?: return false

        val elementBounds = element.boundingClientRect
        val viewportRect = DoubleRectangle.WH(viewportSize)

        // Visible Area + Overscan
        val overscanAmount = viewportRect.dimension.mul((OVERSCAN_FACTOR - 1.0) / 2.0)
        val targetRect = viewportRect
            .inflate(overscanAmount)
            .intersect(elementBounds)
            ?: return false // Element is completely off-screen

        val physicalTargetOrigin = targetRect.origin.mul(contentScale)
        val physicalTargetDimension = targetRect.dimension.mul(contentScale)

        val alignedOrigin = physicalTargetOrigin.floor()
        val fraction = physicalTargetOrigin.subtract(alignedOrigin)

        val bufferSize = physicalTargetDimension
            .add(fraction)
            .ceil()
            .add(CACHE_PADDING_SIZE.mul(2))

        if (bufferSize.x <= 0 || bufferSize.y <= 0) return false

        val canvas = canvasPeer.createCanvas(bufferSize, contentScale = 1.0)
        val ctx = canvas.context2d

        ctx.translate(CACHE_PADDING_SIZE)
        ctx.translate(alignedOrigin.negate())
        ctx.scale(contentScale, contentScale)
        ctx.transform(element.ctm)

        painter.invoke(ctx)

        val snapshot = canvas.takeSnapshot()

        elementCache[element] = CacheEntry(
            snapshot = snapshot,
            snapshotOrigin = alignedOrigin.sub(CACHE_PADDING_SIZE),
            inverseCtm = inverseCtm,
        )
        ctx.dispose()

        return true
    }

    fun paintElement(element: Element, ctx: Context2d) {
        val cacheEntry = elementCache[element] ?: return

        ctx.save()
        ctx.transform(cacheEntry.inverseCtm)
        ctx.scale(1.0 / ctx.contentScale)

        ctx.drawImage(
            snapshot = cacheEntry.snapshot,
            x = cacheEntry.snapshotOrigin.x.toDouble(),
            y = cacheEntry.snapshotOrigin.y.toDouble()
        )

        ctx.restore()
    }

    override fun dispose() {
        elementCache.values.forEach { it.snapshot.dispose() }
        elementCache.clear()
    }

    private class CacheEntry(
        val snapshot: Canvas.Snapshot,
        val snapshotOrigin: Vector, // Top-Left corner in Physical Device Pixels
        val inverseCtm: AffineTransform
    )

    companion object {
        private const val CACHE_PADDING: Int = 2
        private val CACHE_PADDING_SIZE = Vector(CACHE_PADDING, CACHE_PADDING)
        private const val OVERSCAN_FACTOR = 3.0

        private fun DoubleRectangle.expand(w: Double, h: Double): DoubleRectangle {
            val halfW = w / 2.0
            val halfH = h / 2.0
            return DoubleRectangle.LTRB(left - halfW, top - halfH, right + halfW, bottom + halfH)
        }
    }
}