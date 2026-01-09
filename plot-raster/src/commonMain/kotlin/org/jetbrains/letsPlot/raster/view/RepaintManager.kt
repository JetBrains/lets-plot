package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.*
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.raster.scene.Node

internal class RepaintManager(
    private val canvasPeer: CanvasPeer
) : Disposable {
    var overscanFactor: Double = 2.5

    private val nodeCache = mutableMapOf<Node, CacheEntry>()

    fun isCacheValid(node: Node, viewportSize: Vector, contentScale: Double): Boolean {
        if (node.isDirty) {
            return false
        }

        val entry = nodeCache[node] ?: return false

        if (entry.contentScale != contentScale) {
            return false
        }

        val viewportRect = DoubleRectangle.WH(viewportSize)
        val requiredScreenRect = viewportRect.intersect(node.bBoxGlobal) ?: return true

        val inverseCtm = node.ctm.inverse() ?: return false
        val requiredLocalRect = inverseCtm.transform(requiredScreenRect)

        val contains = entry.snapshotLocalBounds.contains(requiredLocalRect)

        return contains
    }

    fun cacheElement(
        node: Node,
        viewportSize: Vector,
        contentScale: Double,
        painter: (Context2d) -> Unit
    ): Boolean {
        val screenToLocalTransform = node.ctm.inverse() ?: return false

        val elementBounds = node.bBoxGlobal
        val overscanAmount = viewportSize.mul((overscanFactor - 1.0) / 2.0)

        val targetRect = DoubleRectangle.WH(viewportSize)
            .inflate(overscanAmount)
            .intersect(elementBounds)
            ?: return false // Element is completely off-screen

        val physicalTargetOrigin = targetRect.origin.mul(contentScale)
        val physicalTargetDimension = targetRect.dimension.mul(contentScale)

        val alignedOrigin = physicalTargetOrigin.floorToVector()

        val bufferSize = physicalTargetDimension
            .ceilToVector()
            .add(CACHE_PADDING_SIZE.mul(2))

        if (bufferSize.x <= 0 || bufferSize.y <= 0) return false

        val canvas = canvasPeer.createCanvas(bufferSize, contentScale = 1.0)
        val ctx = canvas.context2d

        // Since 'physicalTargetOrigin' might be 'alignedOrigin + 0.5',
        // the content will naturally draw at '0.5', preserving sub-pixel positions.
        ctx.translate(alignedOrigin.negate())
        ctx.translate(CACHE_PADDING_SIZE)
        ctx.scale(contentScale)
        ctx.transform(node.ctm)

        painter.invoke(ctx)

        nodeCache[node]?.snapshot?.dispose()
        nodeCache[node] = CacheEntry(
            snapshot = canvas.takeSnapshot(),
            snapshotPhysicalOrigin = alignedOrigin.sub(CACHE_PADDING_SIZE),
            snapshotLocalBounds = screenToLocalTransform.transform(targetRect),
            screenToLocalTransform = screenToLocalTransform,
            contentScale = contentScale
        )

        ctx.dispose()

        return true
    }

    fun paintElement(node: Node, ctx: Context2d) {
        val entry = nodeCache[node] ?: return

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
        nodeCache.values.forEach { it.snapshot.dispose() }
        nodeCache.clear()
    }

    private class CacheEntry(
        val snapshot: Canvas.Snapshot,
        val snapshotPhysicalOrigin: Vector, // in physical pixel coordinates (for context with scale = 1.0)
        val snapshotLocalBounds: DoubleRectangle,
        val screenToLocalTransform: AffineTransform,
        val contentScale: Double
    )

    companion object {
        private const val CACHE_PADDING: Int = 10  // for anti-aliasing artifacts and mitered joins
        private val CACHE_PADDING_SIZE = Vector(CACHE_PADDING, CACHE_PADDING)
    }
}