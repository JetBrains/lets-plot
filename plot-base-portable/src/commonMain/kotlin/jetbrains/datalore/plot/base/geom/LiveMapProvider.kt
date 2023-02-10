/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.base.interact.GeomTargetLocator

interface LiveMapProvider {
    fun createLiveMap(bounds: DoubleRectangle): LiveMapData

    class LiveMapData(
        val canvasFigure: SomeFig,
        // emulate separated layers for TargetPicker so tooltips will work in the same way as in non-livemap plot
        val targetLocators: List<GeomTargetLocator>
    )
}