/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point

import jetbrains.datalore.plot.base.DataPointAesthetics

object TinyPointShape : PointShape {

    override val code: Int
        get() = 46 // ASCII dot `.`

    override fun strokeWidth(dataPoint: DataPointAesthetics): Double {
        return 0.0
    }

    override fun size(dataPoint: DataPointAesthetics): Double {
        return 1.0
    }
}
