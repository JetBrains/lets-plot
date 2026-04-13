/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object BogusCoordinateSystem : CoordinateSystem {
    override val isLinear: Boolean
        get() = error("Not available in a bogus coordinate system")
    override val isPolar: Boolean
        get() = error("Not available in a bogus coordinate system")

    override fun toClient(p: DoubleVector): DoubleVector? {
        error("Not available in a bogus coordinate system")
    }

    override fun fromClient(p: DoubleVector): DoubleVector? {
        error("Not available in a bogus coordinate system")
    }

    override fun unitSize(p: DoubleVector): DoubleVector {
        error("Not available in a bogus coordinate system")
    }

    override fun flip(): CoordinateSystem {
        error("Not available in a bogus coordinate system")
    }
}