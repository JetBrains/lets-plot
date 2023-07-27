/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.core.plot.export\"",
    ReplaceWith("org.jetbrains.letsPlot.core.plot.export.PlotImageExport"), level = DeprecationLevel.WARNING
)
typealias PlotImageExport = org.jetbrains.letsPlot.core.plot.export.PlotImageExport

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.awt.plot\"",
    ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotSvgExport"), level = DeprecationLevel.WARNING
)
typealias PlotSvgExport = org.jetbrains.letsPlot.awt.plot.PlotSvgExport

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.awt.plot\"",
    ReplaceWith("org.jetbrains.letsPlot.awt.plot.MonolithicAwt"), level = DeprecationLevel.WARNING
)
typealias MonolithicAwt = org.jetbrains.letsPlot.awt.plot.MonolithicAwt

