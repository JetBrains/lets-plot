/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas


internal abstract class Element() : Node() {
    var transform: Matrix33 by visualProp(Matrix33.IDENTITY)
    var styleClass: List<String>? by visualProp(null)
    //var clipPath: SkPath? by visualProp(null, managed = true)
    var parent: Container? by visualProp(null)
    var isMouseTransparent: Boolean = true // need proper hitTest for non-rectangular shapes for correct default "false"

    val parents: List<Container> by computedProp(Element::parent) {
        val parents = parent?.parents ?: emptyList()
        parents + listOfNotNull(parent)
    }

    // Not affected by org.jetbrains.skiko.SkiaLayer.getContentScale
    // (see org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView.onRender)
    val ctm: Matrix33 by computedProp(Element::parent, Element::transform) {
        val parentCtm = parent?.ctm ?: Matrix33.IDENTITY
        parentCtm.makeConcat(transform)
    }

    open val localBounds: DoubleRectangle = DoubleRectangle.XYWH(0, 0, 0, 0)

    // Not affected by org.jetbrains.skiko.SkiaLayer.getContentScale
    // (see org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView.onRender)
    open val screenBounds: DoubleRectangle
        get() = ctm.applyTransform(localBounds)

    open fun render(canvas: Canvas) {}

    override fun repr(): String? {
        return ", ctm: ${ctm.repr()}, $screenBounds"
    }
}
