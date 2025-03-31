/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/*
package org.jetbrains.letsPlot.rasterizer.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

internal typealias SkImage = org.jetbrains.skia.Image

internal class Image : Element() {
    var preserveRatio: Boolean by visualProp(false)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
    var img: SkImage? by visualProp(null, managed = true)

    override fun render(canvas: Canvas) {
        img?.let {
            if (preserveRatio) {
                canvas.drawImage(it, x, y)
            } else {
                canvas.drawImageRect(it, Rect.makeXYWH(x, y, width, height))
            }
        }
    }

    override val localBounds: Rect
        get() = Rect.makeXYWH(x, y, width, height)

}

 */