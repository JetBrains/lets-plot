/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.core.plot.export\"",
    ReplaceWith("org.jetbrains.letsPlot.core.plot.export.PlotImageExport"), level = DeprecationLevel.ERROR
)
typealias PlotImageExport = org.jetbrains.letsPlot.core.plot.export.PlotImageExport

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.awt.plot\"",
    ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotSvgExport"), level = DeprecationLevel.ERROR
)
typealias PlotSvgExport = org.jetbrains.letsPlot.awt.plot.PlotSvgExport

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.awt.plot\"",
    ReplaceWith("org.jetbrains.letsPlot.awt.plot.MonolithicAwt"), level = DeprecationLevel.ERROR
)
typealias MonolithicAwt = org.jetbrains.letsPlot.awt.plot.MonolithicAwt

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.awt.plot\"",
    ReplaceWith("org.jetbrains.letsPlot.awt.plot.DisposableJPanel"), level = DeprecationLevel.ERROR
)
typealias DisposableJPanel = org.jetbrains.letsPlot.awt.plot.DisposableJPanel

