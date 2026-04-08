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
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.*
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.SubplotsGrid.DEF_HSPACE
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.SubplotsGrid.DEF_VSPACE
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.GG_TOOLBAR
import org.jetbrains.letsPlot.core.spec.Option.Plot.CAPTION
import org.jetbrains.letsPlot.core.spec.Option.Plot.CAPTION_TEXT
import org.jetbrains.letsPlot.core.spec.Option.Plot.SUBTITLE_TEXT
import org.jetbrains.letsPlot.core.spec.Option.Plot.TAG
import org.jetbrains.letsPlot.core.spec.Option.Plot.TAG_TEXT
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
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.VSPACE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.NAME
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.util.PlotSizeHelper
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK_SHORTHAND as BLANK

class CompositeFigureConfig constructor(
    opts: Map<String, Any>,
    containerTheme: Theme?,
    computationMessagesHandler: ((List<String>) -> Unit)
) : OptionsAccessor(opts) {

    internal val elementConfigs: List<OptionsAccessor?>
    internal val layout: CompositeFigureLayout
    internal val theme: Theme
    internal val guidesSharing: GuidesSharingMode
    internal var collectOverlayLegends: Boolean = false  // whether to collect overlay legends from sub-figures
        private set

    internal val title: String?
        get() = getMap(TITLE)[TITLE_TEXT] as String?
    internal val subtitle: String?
        get() = getMap(TITLE)[SUBTITLE_TEXT] as String?
    internal val caption: String?
        get() = getMap(CAPTION)[CAPTION_TEXT] as String?
    internal val tag: String?
        get() = getMap(TAG)[TAG_TEXT] as String?

    internal val fullTag: String?
        get() {
            val text = tag ?: return null
            return theme.plot().tagPrefix() + text + theme.plot().tagSuffix()
        }

    init {
        val fontFamilyRegistry: FontFamilyRegistry = FontFamilyRegistryConfig(this).createFontFamilyRegistry()
        theme = ThemeConfig(
            themeOptions = getMap(THEME),
            containerTheme = containerTheme,
            fontFamilyRegistry
        ).theme

        @Suppress("UNCHECKED_CAST")
        val figuresSpecs = getList(Option.SubPlots.FIGURES) as List<Any>
        val computationMessages = ArrayList<String>()

        // Propagate toolbar options to sub-figures in case the composite figure has a toolbar.
        val figureSpecsWithToolbarOptions: List<Map<String, Any>?> = figuresSpecs.map { spec ->
            if (spec is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                spec as Map<String, Any>
                // This was necessary to pass the "size_basis" and "size_zoomin" parameters
                // through PlotConfigFrontend to MonolithicCommon, where they will be used
                // when creating the PlotAssembler.
                // https://github.com/JetBrains/lets-plot/blob/f8ead91e83d508550896b5ebc3dd197b039b0cc1/plot-stem/src/commonMain/kotlin/org/jetbrains/letsPlot/core/util/MonolithicCommon.kt#L192
                // Discussed here: https://forum.datalore-plot.jetbrains-boston.com/t/528/6
                // todo: Need refactor. Use CompositeFigureLayout to pass parameters to MonolithicCommon.

                // Add the 'ggtoolbar' option to each subfigure:
                opts[GG_TOOLBAR]?.let { ggToolbar ->
                    spec + (GG_TOOLBAR to ggToolbar)
                } ?: spec
            } else {
                null
            }
        }

        val layoutOptions = OptionsAccessor(getMap(Option.SubPlots.LAYOUT))
        val layoutKind = layoutOptions.getStringSafe(NAME)

        // The "deck" layout: apply the "deck" overlay theme to all sub-figures except the first one.
        val figureSpecsFinal = if (layoutKind == Layout.SUBPLOTS_DECK) {
            val deckShareConfig = DeckScaleShareConfig(layoutOptions)
            figureSpecsWithToolbarOptions.mapIndexed { index, spec ->
                when (index) {
                    0 -> spec  // The "base" figure.
                    else -> spec?.let { applyDeckOverlayTheme(spec, deckShareConfig.shareX, deckShareConfig.shareY) }
                }
            }
        } else {
            figureSpecsWithToolbarOptions
        }

        // Create configs for all sub-figures.
        elementConfigs = figureSpecsFinal.map { spec ->
            spec?.let {
                when (PlotConfig.figSpecKind(spec)) {
                    FigKind.PLOT_SPEC -> PlotConfigFrontend.create(
                        plotSpec = spec,
                        containerTheme = theme
                    ) { computationMessages.addAll(it) }

                    FigKind.SUBPLOTS_SPEC -> CompositeFigureConfig(
                        opts = spec,
                        containerTheme = theme
                    ) { computationMessages.addAll(it) }

                    FigKind.GG_BUNCH_SPEC -> throw IllegalArgumentException("SubPlots can't contain GGBunch.")
                }
            }
        }

        layout = when (layoutKind) {
            Layout.SUBPLOTS_GRID -> createGridLayout(layoutOptions)
            Layout.SUBPLOTS_FREE -> createFreeLayout(layoutOptions, elementConfigs.size)
            Layout.SUBPLOTS_DECK -> createDeckLayout(layoutOptions)
            else -> throw IllegalArgumentException("Unsupported composite figure layout: $layoutKind")
        }

        guidesSharing = when (layoutKind) {
            Layout.SUBPLOTS_DECK -> GuidesSharingMode.COLLECT
            Layout.SUBPLOTS_GRID -> GuidesSharingMode.fromOption(layoutOptions.getString(Layout.GUIDES))
            else -> GuidesSharingMode.KEEP
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
        val shareConfig = GridScaleShareConfig(layoutOptions)
        val scaleShareX: ScaleSharePolicy = shareConfig.shareX
        val scaleShareY: ScaleSharePolicy = shareConfig.shareY

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

    private fun createDeckLayout(layoutOptions: OptionsAccessor): CompositeFigureLayout {
        val deckShareConfig = DeckScaleShareConfig(layoutOptions)
        return CompositeFigureDeckLayout(shareX = deckShareConfig.shareX, shareY = deckShareConfig.shareY)
    }

    companion object {
        private val DECK_OVERLAY_THEME_BASE = mapOf(
            ThemeOption.PLOT_BKGR_RECT to BLANK,
            ThemeOption.PANEL_BKGR_RECT to BLANK,
            ThemeOption.PANEL_BORDER_RECT to BLANK,
            ThemeOption.PANEL_GRID to BLANK,
        )

        private val DECK_OVERLAY_THEME_BLANK_X = mapOf(
            ThemeOption.AXIS_LINE_X to BLANK,
            ThemeOption.AXIS_TICKS_X to BLANK,
            ThemeOption.AXIS_TEXT_X to BLANK,
            ThemeOption.AXIS_TITLE_X to BLANK,
            ThemeOption.AXIS_TOOLTIP_X to BLANK,
        )

        private val DECK_OVERLAY_THEME_BLANK_Y = mapOf(
            ThemeOption.AXIS_LINE_Y to BLANK,
            ThemeOption.AXIS_TICKS_Y to BLANK,
            ThemeOption.AXIS_TEXT_Y to BLANK,
            ThemeOption.AXIS_TITLE_Y to BLANK,
            ThemeOption.AXIS_TOOLTIP_Y to BLANK,
        )

        private fun applyDeckOverlayTheme(spec: Map<String, Any>, shareX: Boolean, shareY: Boolean): Map<String, Any> {
            val blankSharedAxes = when {
                shareX && shareY -> DECK_OVERLAY_THEME_BLANK_X + DECK_OVERLAY_THEME_BLANK_Y
                shareX -> DECK_OVERLAY_THEME_BLANK_X
                shareY -> DECK_OVERLAY_THEME_BLANK_Y
                else -> emptyMap()
            }
            val overlayTheme = DECK_OVERLAY_THEME_BASE + blankSharedAxes

            @Suppress("UNCHECKED_CAST")
            val existingTheme = (spec[THEME] as? Map<String, Any>) ?: emptyMap()
            return spec + (THEME to overlayTheme + existingTheme)
        }
    }

    enum class GuidesSharingMode(val id: String) {
        AUTO(Option.SubPlots.Guides.AUTO),
        COLLECT(Option.SubPlots.Guides.COLLECT),
        KEEP(Option.SubPlots.Guides.KEEP);

        companion object {
            fun fromOption(option: String?): GuidesSharingMode {
                return when (option?.lowercase()) {
                    null,
                    Option.SubPlots.Guides.AUTO -> AUTO

                    Option.SubPlots.Guides.COLLECT -> COLLECT
                    Option.SubPlots.Guides.KEEP -> KEEP
                    else -> throw IllegalArgumentException("'guides'='$option'. Use: 'auto', 'collect', or 'keep'.")
                }
            }
        }
    }
}
