/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.maps.livemap.entities.rendering

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.entities.rendering.StyleComponent

object Common {

    fun apply(styleComponent: StyleComponent, ctx: Context2d) {
        ctx.setFillStyle(styleComponent.fillColor)
        ctx.setStrokeStyle(styleComponent.strokeColor)
        ctx.setLineWidth(styleComponent.strokeWidth)
    }
}
