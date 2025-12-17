package org.jetbrains.letsPlot.raster.view

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

    fun cacheElement(element: Element, viewportSize: Vector, painter: (Context2d) -> Unit) {
        val elementScreenBBox = element.boundingClientRect
        val elementIntPos = floor(elementScreenBBox.origin)
        val snapshotPos = elementIntPos.sub(Vector(CACHE_PADDING, CACHE_PADDING))
        val snapshotSize = ceil(elementScreenBBox.dimension.add(DoubleVector(2 * CACHE_PADDING, 2 * CACHE_PADDING)))
        val snapshotPixelGridOffset = elementScreenBBox.origin.subtract(elementIntPos)

        val canvas = canvasPeer.createCanvas(snapshotSize)
        val ctx = canvas.context2d
        ctx.translate(elementScreenBBox.origin.negate()) // move element to (0,0) in canvas space
        ctx.translate(CACHE_PADDING, CACHE_PADDING) // padding for anti-aliasing
        ctx.translate(snapshotPixelGridOffset) // snapshot alignment for pixel grid
        ctx.transform(element.transform) // apply element transform
        painter.invoke(ctx)
        val snapshot = canvas.takeSnapshot()
        elementCache[element] = CacheEntry(element, snapshot, snapshotPos, snapshotSize)
        ctx.dispose()
    }

    fun getElementSnapshot(element: Element): Canvas.Snapshot? {
        return elementCache[element]?.snapshot
    }

    fun releaseElementCache(element: Element) {
        val entry = elementCache.remove(element)
        entry?.snapshot?.dispose()
    }

    fun paintElement(
        element: Element,
        context2d: Context2d
    ) {
        val cacheEntry = elementCache[element]
        if (cacheEntry != null) {
            context2d.drawImage(
                snapshot = cacheEntry.snapshot,
                x = cacheEntry.snapshotPos.x.toDouble(),
                y = cacheEntry.snapshotPos.y.toDouble(),
                dw = cacheEntry.snapshotSize.x.toDouble(),
                dh = cacheEntry.snapshotSize.y.toDouble()
            )
        } else {
            error("Element is not cached: $element")
        }
    }

    override fun dispose() {
        elementCache.values.forEach { it.snapshot.dispose() }
        elementCache.clear()
    }

    private class CacheEntry(
        val element: Element,
        val snapshot: Canvas.Snapshot,
        val snapshotPos: Vector,
        val snapshotSize: Vector
    )

    companion object {
        private const val CACHE_PADDING: Int = 2
        private const val OVERSCAN_FACTOR = 1.5
    }
}