/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem

internal class NoneSelectionStrategy : DataSelectionStrategy {

    override fun clientRectToDataBounds(clientRect: DoubleRectangle, coord: CoordinateSystem): DoubleRectangle {
        UNSUPPORTED("Not supported in NoneSelectionStrategy.")
    }
}