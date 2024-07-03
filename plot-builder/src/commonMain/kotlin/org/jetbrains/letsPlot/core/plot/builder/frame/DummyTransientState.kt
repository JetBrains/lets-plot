/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.builder.ComponentTransientState

internal class DummyTransientState : ComponentTransientState(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) {
    override val dataBounds: DoubleRectangle
        get() = UNSUPPORTED("Not yet implemented")

    override fun repaint() {
        UNSUPPORTED("Not yet implemented")
    }
}