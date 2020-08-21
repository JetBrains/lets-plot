/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange

internal class CartesianCoordProvider(
    xLim: ClosedRange<Double>?,
    yLim: ClosedRange<Double>?
) : CoordProviderBase(xLim, yLim)