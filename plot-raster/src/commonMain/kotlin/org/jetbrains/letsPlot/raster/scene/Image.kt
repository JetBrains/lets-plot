/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d


internal class Image : Node() {
    var preserveRatio: Boolean by variableAttr(false)
    var x: Float by variableAttr(0f)
    var y: Float by variableAttr(0f)
    var width: Float by variableAttr(0f)
    var height: Float by variableAttr(0f)
    var img: Bitmap? by variableAttr(null)

    private val snapshot: Canvas.Snapshot? by derivedAttr {
        val peer = peer ?: return@derivedAttr null
        val image = img ?: return@derivedAttr null

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

    override fun onDetach() {
        snapshot?.dispose()
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        return DoubleRectangle.XYWH(x, y, width, height)
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(Image::class)

        val PreserveRatioAttrSpec = CLASS.registerVariableAttr(Image::preserveRatio, affectsBBox = false)
        val XAttrSpec = CLASS.registerVariableAttr(Image::x, affectsBBox = true)
        val YAttrSpec = CLASS.registerVariableAttr(Image::y, affectsBBox = true)
        val WidthAttrSpec = CLASS.registerVariableAttr(Image::width, affectsBBox = true)
        val HeightAttrSpec = CLASS.registerVariableAttr(Image::height, affectsBBox = true)
        val ImgAttrSpec = CLASS.registerVariableAttr(Image::img, affectsBBox = false)

        val SnapshotAttrSpec = CLASS.registerDerivedAttr(kProp = Image::snapshot, dependencies = setOf(ImgAttrSpec, PeerAttrSpec))
    }
}
