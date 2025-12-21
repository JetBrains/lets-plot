package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.math.ceil
import org.jetbrains.letsPlot.commons.intern.math.floor
import org.jetbrains.letsPlot.commons.intern.math.mul
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.raster.shape.Element

internal class RepaintManager(
    private val canvasPeer: CanvasPeer
) : Disposable {
    private val elementCache = mutableMapOf<Element, CacheEntry>()

    fun isCacheValid(element: Element, viewportSize: Vector, contentScale: Double): Boolean {
        if (element.isDirty) {
            return false
        }

        val entry = elementCache[element] ?: return false

        if (entry.contentScale != contentScale) {
            return false
        }

        val viewportRect = DoubleRectangle.WH(viewportSize)
        val requiredScreenRect = viewportRect.intersect(element.bBoxGlobal) ?: return true

        val inverseCtm = element.ctm.inverse() ?: return false
        val requiredLocalRect = inverseCtm.transform(requiredScreenRect)

        return entry.snapshotLocalBounds.contains(requiredLocalRect)
    }

    fun cacheElement(
        element: Element,
        viewportSize: Vector,
        contentScale: Double,
        painter: (Context2d) -> Unit
    ): Boolean {
        val screenToLocalTransform = element.ctm.inverse() ?: return false

        val elementBounds = element.bBoxGlobal
        val overscanAmount = viewportSize.mul((OVERSCAN_FACTOR - 1.0) / 2.0)

        val targetRect = DoubleRectangle.WH(viewportSize)
            .inflate(overscanAmount)
            .intersect(elementBounds)
            ?: return false // Element is completely off-screen

        val physicalTargetOrigin = targetRect.origin.mul(contentScale)
        val physicalTargetDimension = targetRect.dimension.mul(contentScale)

        val alignedOrigin = physicalTargetOrigin.floor()

        val bufferSize = physicalTargetDimension
            .ceil()
            .add(CACHE_PADDING_SIZE.mul(2))

        if (bufferSize.x <= 0 || bufferSize.y <= 0) return false

        val canvas = canvasPeer.createCanvas(bufferSize, contentScale = 1.0)
        val ctx = canvas.context2d

        // Since 'physicalTargetOrigin' might be 'alignedOrigin + 0.5',
        // the content will naturally draw at '0.5', preserving sub-pixel positions.
        ctx.translate(alignedOrigin.negate())
        ctx.translate(CACHE_PADDING_SIZE)
        ctx.scale(contentScale)
        ctx.transform(element.ctm)

        painter.invoke(ctx)

        elementCache[element]?.snapshot?.dispose()
        elementCache[element] = CacheEntry(
            snapshot = canvas.takeSnapshot(),
            snapshotPhysicalOrigin = alignedOrigin.sub(CACHE_PADDING_SIZE),
            snapshotLocalBounds = screenToLocalTransform.transform(targetRect),
            screenToLocalTransform = screenToLocalTransform,
            contentScale = contentScale
        )

        ctx.dispose()

        return true
    }

    fun paintElement(element: Element, ctx: Context2d) {
        val entry = elementCache[element] ?: return

        ctx.save()
        ctx.transform(entry.screenToLocalTransform)
        ctx.scale(1.0 / entry.contentScale) // to physical pixel coords
        ctx.drawImage(
            snapshot = entry.snapshot,
            x = entry.snapshotPhysicalOrigin.x.toDouble(),
            y = entry.snapshotPhysicalOrigin.y.toDouble()
        )
        ctx.restore()
    }

    override fun dispose() {
        elementCache.values.forEach { it.snapshot.dispose() }
        elementCache.clear()
    }

    private class CacheEntry(
        val snapshot: Canvas.Snapshot,
        val snapshotPhysicalOrigin: Vector, // in physical pixel coordinates (for context with scale = 1.0)
        val snapshotLocalBounds: DoubleRectangle,
        val screenToLocalTransform: AffineTransform,
        val contentScale: Double
    )

    companion object {
        private const val CACHE_PADDING: Int = 2
        private val CACHE_PADDING_SIZE = Vector(CACHE_PADDING, CACHE_PADDING)
        private const val OVERSCAN_FACTOR = 2.5
    }
}