/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem

internal class MouseWheelSelectionStrategy : DataSelectionStrategy {

    override fun clientRectToDataBounds(clientRect: DoubleRectangle, coord: CoordinateSystem): DoubleRectangle {
        val domainPoint0 = coord.fromClient(clientRect.origin)
            ?: error("Can't translate client ${clientRect.origin} to data domain.")

        val clientBottomRight = clientRect.origin.add(clientRect.dimension)
        val domainPoint1 = coord.fromClient(clientBottomRight)
            ?: error("Can't translate client $clientBottomRight to data domain.")

        val dataBounds = DoubleRectangle.span(domainPoint0, domainPoint1).let {
            DataBoundsFix.unImplode(it)
        }

        return dataBounds
    }
}