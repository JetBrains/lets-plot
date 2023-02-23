/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.CompositeFigureGridAlignmentLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.CompositeFigureGridLayout
import jetbrains.datalore.plot.builder.presentation.Defaults.SubplotsGrid.DEF_HSPACE
import jetbrains.datalore.plot.builder.presentation.Defaults.SubplotsGrid.DEF_VSPACE
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.COL_WIDTHS
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.FIT_CELL_ASPECT_RATIO
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.HSPACE
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.INNER_ALIGNMENT
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.NCOLS
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.NROWS
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.ROW_HEIGHTS
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.VSPACE
import jetbrains.datalore.plot.config.Option.SubPlots.Layout
import jetbrains.datalore.plot.config.Option.SubPlots.Layout.NAME

internal class CompositeFigureConfig(
    opts: Map<String, Any>,
    computationMessagesHandler: ((List<String>) -> Unit)
) : OptionsAccessor(opts) {

    val elementConfigs: List<OptionsAccessor?>

    init {
        @Suppress("UNCHECKED_CAST")
        val figuresSpecs = getList(Option.SubPlots.FIGURES) as List<Any>
        val computationMessages = ArrayList<String>()
        elementConfigs = figuresSpecs.map { spec ->
            if (spec is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                spec as Map<String, Any>
                when (PlotConfig.figSpecKind(spec)) {
                    FigKind.PLOT_SPEC -> PlotConfigClientSide.create(spec) { computationMessages.addAll(it) }
                    FigKind.SUBPLOTS_SPEC -> CompositeFigureConfig(spec) { computationMessages.addAll(it) }
                    FigKind.GG_BUNCH_SPEC -> throw IllegalArgumentException("SubPlots can't contain GGBunch.")
                }
            } else {
                null
            }
        }

        computationMessagesHandler(computationMessages)
    }

    fun createLayout(): CompositeFigureLayout {
        val layoutOptions = OptionsAccessor(getMap(Option.SubPlots.LAYOUT))
        val layoutKind = layoutOptions.getStringSafe(NAME)

        if (layoutKind == Layout.SUBPLOTS_GRID) {
            val (ncols, nrows) = gridSizeOrNull()!!
            val hSpace = layoutOptions.getDoubleDef(HSPACE, DEF_HSPACE)
            val vSpace = layoutOptions.getDoubleDef(VSPACE, DEF_VSPACE)
            val colWidths = layoutOptions.getDoubleList(COL_WIDTHS)
            val rowHeights = layoutOptions.getDoubleList(ROW_HEIGHTS)
            val fitAspectCellAspectRatio = layoutOptions.getBoolean(FIT_CELL_ASPECT_RATIO, false)
            val innerAlignment = layoutOptions.getBoolean(INNER_ALIGNMENT, false)
            return if (innerAlignment) {
                CompositeFigureGridAlignmentLayout(
                    ncols = ncols,
                    nrows = nrows,
                    hSpace = hSpace,
                    vSpace = vSpace,
                    colWidths = colWidths,
                    rowHeights = rowHeights,
                )
            } else {
                CompositeFigureGridLayout(
                    ncols = ncols,
                    nrows = nrows,
                    hSpace = hSpace,
                    vSpace = vSpace,
                    colWidths = colWidths,
                    rowHeights = rowHeights,
                )
            }
        }

        throw IllegalArgumentException("Unsupported composit figure layout: $layoutKind")
    }

    fun gridSizeOrNull(): Pair<Int, Int>? {
        val layoutOptions = OptionsAccessor(getMap(Option.SubPlots.LAYOUT))
        val layoutKind = layoutOptions.getStringSafe(NAME)
        return if (layoutKind == Layout.SUBPLOTS_GRID) {
            val ncols = layoutOptions.getIntegerSafe(NCOLS)
            val nrows = layoutOptions.getIntegerSafe(NROWS)
            Pair(ncols, nrows)
        } else {
            null
        }
    }
}
