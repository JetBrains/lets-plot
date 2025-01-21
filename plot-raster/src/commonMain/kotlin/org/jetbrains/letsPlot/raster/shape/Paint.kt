/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.values.Color

internal class Paint : Managed {
    var isStroke: Boolean = false
    var color: Color = Color.BLACK
    var strokeWidth: Float = 1f
    var strokeMiter: Float = 10f
    var strokeDashList: DoubleArray = doubleArrayOf()
    var strokeDashOffset: Float = 0f

    private var _isClosed = false

    override fun close() {
        _isClosed = true
    }

    override fun isClosed(): Boolean {
        return _isClosed
    }
}
