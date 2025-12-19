package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
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
        val physicalOrigin = elementBounds.origin.mul(contentScale)
        val physicalDimension = elementBounds.dimension.mul(contentScale)

        val fraction = physicalOrigin.subtract(physicalOrigin.floor())
        val bufferSize = physicalDimension
            .add(fraction)
            .ceil()
            .add(CACHE_PADDING_SIZE.mul(2))

        if (bufferSize.x <= 0 || bufferSize.y <= 0) return false

        val canvas = canvasPeer.createCanvas(bufferSize, contentScale = 1.0)
        val ctx = canvas.context2d

        // From Logical Screen Space -> Local Space
        ctx.translate(physicalOrigin.floor().negate().toDoubleVector())
        ctx.translate(CACHE_PADDING_SIZE.toDoubleVector())
        ctx.scale(contentScale, contentScale)
        ctx.transform(element.ctm)

        painter.invoke(ctx)

        val snapshot = canvas.takeSnapshot()

        elementCache[element] = CacheEntry(
            element = element,
            snapshot = snapshot,
            physicalOrigin = physicalOrigin.floor().sub(CACHE_PADDING_SIZE),
            inverseCtm = inverseCtm,
            bufferSize = bufferSize
        )
        ctx.dispose()

        return true
    }

    fun paintElement(element: Element, ctx: Context2d) {
        val cacheEntry = elementCache[element] ?: return

        ctx.save()

        // From Local Space -> Logical Screen Space
        ctx.transform(cacheEntry.inverseCtm)
        ctx.scale(1.0 / ctx.contentScale)

        ctx.drawImage(
            snapshot = cacheEntry.snapshot,
            x = cacheEntry.physicalOrigin.x.toDouble(),
            y = cacheEntry.physicalOrigin.y.toDouble(),
            dw = cacheEntry.bufferSize.x.toDouble(),
            dh = cacheEntry.bufferSize.y.toDouble()
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
        val physicalOrigin: Vector,
        val inverseCtm: AffineTransform,
        val bufferSize: Vector
    )

    companion object {
        private const val CACHE_PADDING: Int = 2
        private val CACHE_PADDING_SIZE = Vector(CACHE_PADDING, CACHE_PADDING)
    }
}