/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureFreeLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureGridAlignmentLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureGridLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.ScaleSharePolicy
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.SubplotsGrid.DEF_HSPACE
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.SubplotsGrid.DEF_VSPACE
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Plot.CAPTION
import org.jetbrains.letsPlot.core.spec.Option.Plot.CAPTION_TEXT
import org.jetbrains.letsPlot.core.spec.Option.Plot.SUBTITLE_TEXT
import org.jetbrains.letsPlot.core.spec.Option.Plot.THEME
import org.jetbrains.letsPlot.core.spec.Option.Plot.TITLE
import org.jetbrains.letsPlot.core.spec.Option.Plot.TITLE_TEXT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Free
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.COL_WIDTHS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.FIT_CELL_ASPECT_RATIO
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.HSPACE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.INNER_ALIGNMENT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NCOLS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NROWS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.ROW_HEIGHTS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.SHARE_X_SCALE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.SHARE_Y_SCALE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_ALL
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_COL
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_NONE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_ROW
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.VSPACE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.NAME
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.util.PlotSizeHelper

class CompositeFigureConfig constructor(
    opts: Map<String, Any>,
    containerTheme: Theme?,
    computationMessagesHandler: ((List<String>) -> Unit)
) : OptionsAccessor(opts) {

    internal val elementConfigs: List<OptionsAccessor?>
    internal val layout: CompositeFigureLayout
    internal val theme: Theme

    internal val title: String?
        get() = getMap(TITLE)[TITLE_TEXT] as String?
    internal val subtitle: String?
        get() = getMap(TITLE)[SUBTITLE_TEXT] as String?
    internal val caption: String?
        get() = getMap(CAPTION)[CAPTION_TEXT] as String?

    init {
        val fontFamilyRegistry: FontFamilyRegistry = FontFamilyRegistryConfig(this).createFontFamilyRegistry()
        val ownTheme = ThemeConfig(getMap(THEME), fontFamilyRegistry).theme
        theme = if (containerTheme == null || hasOwn(THEME)) {
            ownTheme
        } else {
            ownTheme.toInherited(containerTheme)
        }

        @Suppress("UNCHECKED_CAST")
        val figuresSpecs = getList(Option.SubPlots.FIGURES) as List<Any>
        val computationMessages = ArrayList<String>()
        elementConfigs = figuresSpecs.map { spec ->
            if (spec is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                spec as Map<String, Any>
                when (PlotConfig.figSpecKind(spec)) {
                    FigKind.PLOT_SPEC -> PlotConfigFrontend.create(spec, theme) { computationMessages.addAll(it) }
                    FigKind.SUBPLOTS_SPEC -> CompositeFigureConfig(spec, theme) { computationMessages.addAll(it) }
                    FigKind.GG_BUNCH_SPEC -> throw IllegalArgumentException("SubPlots can't contain GGBunch.")
                }
            } else {
                null
            }
        }

        val layoutOptions = OptionsAccessor(getMap(Option.SubPlots.LAYOUT))
        val layoutKind = layoutOptions.getStringSafe(NAME)
        layout = when (layoutKind) {
            Layout.SUBPLOTS_GRID -> createGridLayout(layoutOptions)
            Layout.SUBPLOTS_FREE -> createFreeLayout(layoutOptions, elementConfigs.size)
            else -> throw IllegalArgumentException("Unsupported composit figure layout: $layoutKind")
        }

        computationMessagesHandler(computationMessages)
    }

    private fun createGridLayout(layoutOptions: OptionsAccessor): CompositeFigureLayout {
        val ncols = layoutOptions.getIntegerSafe(NCOLS)
        val nrows = layoutOptions.getIntegerSafe(NROWS)
        val hSpace = layoutOptions.getDoubleDef(HSPACE, DEF_HSPACE)
        val vSpace = layoutOptions.getDoubleDef(VSPACE, DEF_VSPACE)
        val colWidths = layoutOptions.getDoubleList(COL_WIDTHS)
        val rowHeights = layoutOptions.getDoubleList(ROW_HEIGHTS)
        val fitCellAspectRatio = layoutOptions.getBoolean(FIT_CELL_ASPECT_RATIO, true)
        val innerAlignment = layoutOptions.getBoolean(INNER_ALIGNMENT, false)
        val scaleShareX: ScaleSharePolicy = asScaleSharePolicy(SHARE_X_SCALE, layoutOptions)
        val scaleShareY: ScaleSharePolicy = asScaleSharePolicy(SHARE_Y_SCALE, layoutOptions)

        val elementsDefaultSizes: List<DoubleVector?> = elementConfigs.map { figureSpec ->
            figureSpec?.let {
                if (!fitCellAspectRatio) {
                    PlotSizeHelper.singlePlotSizeDefault(
                        plotSpec = it.toMap(),
                        facets = PlotFacets.UNDEFINED,
                        containsLiveMap = false
                    )
                } else {
                    null
                }
            }
        }

        return if (innerAlignment) {
            CompositeFigureGridAlignmentLayout(
                ncols = ncols,
                nrows = nrows,
                hSpace = hSpace,
                vSpace = vSpace,
                colWidths = colWidths,
                rowHeights = rowHeights,
                fitCellAspectRatio = fitCellAspectRatio,
                elementsDefaultSizes = elementsDefaultSizes,
                scaleShareX = scaleShareX,
                scaleShareY = scaleShareY,
            )
        } else {
            CompositeFigureGridLayout(
                ncols = ncols,
                nrows = nrows,
                hSpace = hSpace,
                vSpace = vSpace,
                colWidths = colWidths,
                rowHeights = rowHeights,
                fitCellAspectRatio = fitCellAspectRatio,
                elementsDefaultSizes = elementsDefaultSizes,
                scaleShareX = scaleShareX,
                scaleShareY = scaleShareY,
            )
        }
    }

    private fun asScaleSharePolicy(option: String, layoutOptions: OptionsAccessor): ScaleSharePolicy {
        return layoutOptions.get(option)?.let {
            when (it.toString().lowercase()) {
                SHARE_NONE -> ScaleSharePolicy.NONE
                SHARE_ALL -> ScaleSharePolicy.ALL
                SHARE_ROW -> ScaleSharePolicy.ROW
                SHARE_COL -> ScaleSharePolicy.COL
                else -> throw IllegalArgumentException("Unexpected value: '$option = $it'. Use: 'all', 'row', 'col' or 'none'")
            }
        } ?: ScaleSharePolicy.NONE
    }

    private fun createFreeLayout(
        layoutOptions: OptionsAccessor,
        elementsCount: Int
    ): CompositeFigureLayout {

        val regionOptionsList = layoutOptions.getList(Free.REGIONS)
        val (regions, offsets) = regionOptionsList.map { region ->
            check(
                region is List<*>
                        && region.size in listOf(4, 6)
                        && region.all { it is Number }) {
                "'region' in 'free' layout must be a list of 4 or 6 numbers, was: $region}"
            }
            @Suppress("UNCHECKED_CAST")
            region as List<Number>

            Pair(
                DoubleRectangle.XYWH(
                    x = region[0].toDouble(),
                    y = region[1].toDouble(),
                    width = region[2].toDouble(),
                    height = region[3].toDouble()
                ),
                DoubleVector(
                    x = if (region.size > 4) region[4].toDouble() else 0.0,
                    y = if (region.size > 5) region[5].toDouble() else 0.0
                )
            )
        }.unzip()

        return CompositeFigureFreeLayout(regions, offsets, elementsCount)
    }
}
