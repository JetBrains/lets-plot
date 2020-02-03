/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange

internal class CartesianCoordProvider(
    private val xLim: ClosedRange<Double>?,
    private val yLim: ClosedRange<Double>?
) : CoordProviderBase(xLim, yLim)