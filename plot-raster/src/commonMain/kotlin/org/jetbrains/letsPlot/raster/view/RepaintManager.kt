package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.math.ceil
import org.jetbrains.letsPlot.commons.intern.math.floor
import org.jetbrains.letsPlot.commons.intern.math.subtract
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.raster.shape.Element

internal class RepaintManager(
    private val canvasPeer: CanvasPeer
): Disposable {
    private val elementCache = mutableMapOf<Element, CacheEntry>()

    fun containsElement(element: Element): Boolean {
        return elementCache.containsKey(element)
    }

    fun cacheElement(element: Element, viewportSize: Vector, contentScale: Double, painter: (Context2d) -> Unit): Boolean {
        val snapshotInverseCtm = element.ctm.inverse() ?: return false

        val elementScreenBBox = element.boundingClientRect

        val elementPos = elementScreenBBox.origin
        val scaledElementPos = elementPos.mul(contentScale)
        val elementSize = elementScreenBBox.dimension

        val scaledElementIntPos = floor(scaledElementPos)
        val scaledSubpixelOffset = scaledElementPos.subtract(scaledElementIntPos)

        val snapshotSize = elementSize
            .add(DoubleVector(2 * CACHE_PADDING, 2 * CACHE_PADDING))
            .mul(contentScale)

        val canvas = canvasPeer.createCanvas(ceil(snapshotSize), contentScale = 1.0)
        val ctx = canvas.context2d
        ctx.translate(scaledElementPos.negate()) // move element to (0,0) in canvas space
        ctx.translate(CACHE_PADDING, CACHE_PADDING) // padding for anti-aliasing
        ctx.translate(scaledSubpixelOffset) // snapshot alignment for pixel grid
        ctx.scale(contentScale, contentScale)
        ctx.transform(element.ctm) // apply element transform
        painter.invoke(ctx)
        val snapshot = canvas.takeSnapshot()

        elementCache[element] = CacheEntry(
            element = element,
            snapshot = snapshot,
            snapshotPos = scaledElementIntPos.sub(Vector(CACHE_PADDING, CACHE_PADDING)),
            snapshotInverseCtm = snapshotInverseCtm
        )
        ctx.dispose()

        return true
    }

    fun paintElement(element: Element, ctx: Context2d) {
        val cacheEntry = elementCache[element] ?: return

        ctx.save()
        // While panning/zooming, we need to apply the inverse transform to keep the cached snapshot in place
        ctx.transform(cacheEntry.snapshotInverseCtm)
        // Scale to 1.0 - snapshot image and positions are in device pixels
        ctx.scale(1.0 / ctx.contentScale)
        ctx.drawImage(
            snapshot = cacheEntry.snapshot,
            x = cacheEntry.snapshotPos.x.toDouble(),
            y = cacheEntry.snapshotPos.y.toDouble(),
            dw = cacheEntry.snapshot.size.x.toDouble(),
            dh = cacheEntry.snapshot.size.y.toDouble()
        )
        ctx.restore()
    }

    override fun dispose() {
        elementCache.values.forEach { it.snapshot.dispose() }
        elementCache.clear()
    }

    private class CacheEntry(
        val element: Element,
        val snapshot: Canvas.Snapshot,
        val snapshotPos: Vector,
        val snapshotInverseCtm: AffineTransform
    )

    companion object {
        private const val CACHE_PADDING: Int = 2
        private const val OVERSCAN_FACTOR = 1.5
    }
}