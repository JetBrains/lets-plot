/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas


internal class Image : Element() {
    var preserveRatio: Boolean by visualProp(false)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
    var img: Bitmap? by visualProp(null)

    private val snapshot: Canvas.Snapshot? by computedProp(Image::img, Node::peer) {
        val peer = peer ?: return@computedProp null
        val image = img ?: return@computedProp null

        peer.canvasProvider.createSnapshot(image)
    }

    override fun render(canvas: Canvas) {
        val snapshot = snapshot ?: return
        if (preserveRatio) {
            canvas.context2d.drawImage(snapshot, x.toDouble(), y.toDouble())
        } else {
            canvas.context2d.drawImage(snapshot, x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(x, y, width, height)

}
