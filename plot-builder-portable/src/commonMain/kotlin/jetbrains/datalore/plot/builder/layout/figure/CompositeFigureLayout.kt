/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.config.FigureBuildInfo

interface CompositeFigureLayout {
    fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo>
}