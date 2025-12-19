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

    // NEW: Check if the current visible area is fully covered by the cache
    fun isCacheValid(element: Element, viewportSize: Vector): Boolean {
        if (element.isDirty) return false
        //return element in elementCache
        val entry = elementCache[element] ?: return false

        // 1. Calculate the 'Required Area' in Screen Space (Viewport intersection)
        val viewportRect = DoubleRectangle.WH(viewportSize)
        val elementBounds = element.boundingClientRect
        val requiredScreenRect = viewportRect.intersect(elementBounds) ?: return true

        // 2. Project 'Required Area' to Local Space
        // We must compare in Local Space because the element might have panned,
        // shifting the Screen coordinates relative to the cached content.
        val currentInverseCtm = element.ctm.inverse() ?: return false
        val requiredLocalRect = currentInverseCtm.transform(requiredScreenRect)

        // 3. Check containment
        return entry.cachedLocalBounds.contains(requiredLocalRect)
    }

    fun cacheElement(
        element: Element,
        viewportSize: Vector,
        contentScale: Double,
        painter: (Context2d) -> Unit
    ): Boolean {
        println("RepaintManager: Caching element id='${element.id}'")
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

        // Calculate the Local Bounds of what we just cached
        // targetRect is the Screen Space area we wanted.
        val cachedLocalBounds = inverseCtm.transform(targetRect)

        elementCache[element] = CacheEntry(
            snapshot = snapshot,
            snapshotOrigin = alignedOrigin.sub(CACHE_PADDING_SIZE),
            inverseCtm = inverseCtm,
            cachedLocalBounds = cachedLocalBounds
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
        val snapshotOrigin: Vector,
        val inverseCtm: AffineTransform,
        val cachedLocalBounds: DoubleRectangle // Added: The local area covered by this cache
    )

    companion object {
        private const val CACHE_PADDING: Int = 2
        private const val OVERSCAN_FACTOR = 1.1
        private val CACHE_PADDING_SIZE = Vector(CACHE_PADDING, CACHE_PADDING)
    }
}