/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model

import jetbrains.datalore.base.geometry.DoubleVector

open class PlotConfigDemoBase(plotSize: DoubleVector = DEFAULT_PLOT_SIZE) : SimpleDemoBase(plotSize) {
    companion object {
        private val DEFAULT_PLOT_SIZE = DoubleVector(400.0, 300.0)
    }
}