/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import java.awt.Graphics

object CanvasUtil {
    private const val DEVICE_PIXEL_RATIO_NAME = "DEVICE_PIXEL_RATIO"

    fun readDevicePixelRatio(defaultValue: Double): Double {
        return if (System.getProperties().containsKey(DEVICE_PIXEL_RATIO_NAME)) {
            System.getProperty(DEVICE_PIXEL_RATIO_NAME).toDouble()
        } else defaultValue
    }

    fun drawGraphicsCanvasControl(graphicsCanvasControl: GraphicsCanvasControl, g: Graphics) {
        val image = graphicsCanvasControl.image
        if (image != null) {
            val size = graphicsCanvasControl.size
            @Suppress("UNUSED_ANONYMOUS_PARAMETER")
            g.drawImage(image, 0, 0, size.x, size.y) {
                img,
                infoflags,
                x,
                y,
                width,
                height -> true }
        }
    }
}
