/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d


internal class Image : Element() {
    var preserveRatio: Boolean by visualProp(false)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
    var img: Bitmap? by visualProp(null)

    private val snapshot: Canvas.Snapshot? by computedProp(Image::img, Element::peer) {
        val peer = peer ?: return@computedProp null
        val image = img ?: return@computedProp null

        peer.canvasPeer.createSnapshot(image)
    }

    override fun render(ctx: Context2d) {
        val snapshot = snapshot ?: return
        if (preserveRatio) {
            ctx.drawImage(snapshot, x.toDouble(), y.toDouble())
        } else {
            ctx.drawImage(snapshot, x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override fun onDetached() {
        snapshot?.dispose()
    }

    override val bBox: DoubleRectangle
        get() = DoubleRectangle.XYWH(x, y, width, height)
}
