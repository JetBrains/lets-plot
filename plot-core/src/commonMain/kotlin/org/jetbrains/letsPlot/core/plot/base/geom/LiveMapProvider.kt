/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator

interface LiveMapProvider {
    fun createLiveMap(bounds: DoubleRectangle): LiveMapData

    class LiveMapData(
        val canvasFigure: SomeFig,
        // emulate separated layers for TargetPicker so tooltips will work in the same way as in non-livemap plot
        val targetLocators: List<GeomTargetLocator>
    )
}