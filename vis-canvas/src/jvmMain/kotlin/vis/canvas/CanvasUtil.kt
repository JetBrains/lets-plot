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

}
