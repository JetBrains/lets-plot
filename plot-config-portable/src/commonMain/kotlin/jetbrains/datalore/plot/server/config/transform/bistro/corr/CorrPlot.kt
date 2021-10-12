/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.letsPlot.bistro.corr

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.ScaleConfig
import jetbrains.datalore.plot.config.ScaleConfig.Companion.COLOR_BREWER
import jetbrains.datalore.plot.config.ScaleConfig.Companion.COLOR_GRADIENT2
import jetbrains.datalore.plot.server.config.transform.bistro.*
import jetbrains.datalore.plot.server.config.transform.bistro.TooltipsOptions.Companion.variable
import jetbrains.datalore.plot.server.config.transform.bistro.TooltipsOptions.Format
import jetbrains.datalore.plot.server.config.transform.bistro.corr.DataUtil.standardiseData
import jetbrains.datalore.plot.server.config.transform.bistro.corr.Option.Corr.Layer.Type.FULL
import jetbrains.datalore.plot.server.config.transform.bistro.corr.Option.Corr.Layer.Type.LOWER
import jetbrains.datalore.plot.server.config.transform.bistro.corr.Option.Corr.Layer.Type.UPPER
import jetbrains.letsPlot.bistro.corr.CorrUtil.correlations
import jetbrains.letsPlot.bistro.corr.CorrUtil.correlationsToDataframe
import jetbrains.letsPlot.bistro.corr.CorrUtil.matrixXYSeries
import jetbrains.letsPlot.bistro.corr.Method.correlationPearson
import jetbrains.letsPlot.bistro.corr.OptionsConfigurator.getKeepMatrixDiag
import kotlin.math.max
import kotlin.math.min

/**
 * Correlation plot builder.
 *
 * The terminal 'build()' method will create a fully configured 'Plot' (i.e. Figure) object.
 *
 * @param data Dataframe to compute correlations on.
 * @param title Plot title.
 * @param showLegend Whether to show a legend.
 * @param flip Whether to flip the y axis.
 * @param threshold Minimal correlation abs value to be included in result. Must be in interval [0.0, 1.0]
 * @param adjustSize A scaler to adjust the plot size which was computed by `CorrPlot` automatically.
 */
class CorrPlot private constructor(
    private val data: Map<*, *>,
    private val title: String? = null,
    private val showLegend: Boolean,
    private val flip: Boolean,
    private val threshold: Double,
    private val adjustSize: Double,
    private val tiles: LayerParams,
    private val points: LayerParams,
    private val labels: LayerParams,
    private var colorScaleOptions: ScaleOptions,
    private var fillScaleOptions: ScaleOptions
) {

    constructor(
        data: Map<*, *>,
        title: String? = null,
        showLegend: Boolean? = null,
        flip: Boolean? = null,
        threshold: Double? = null,
        adjustSize: Double? = null,
    ) : this(
        data,
        title,
        showLegend ?: true,
        flip ?: true,
        threshold ?: DEF_THRESHOLD,
        adjustSize ?: 1.0,
        tiles = LayerParams(),
        points = LayerParams(),
        labels = LayerParams(),
        colorScaleOptions = scaleGradient(Aes.COLOR, DEF_LOW_COLOR, DEF_MID_COLOR, DEF_HIGH_COLOR),
        fillScaleOptions = scaleGradient(Aes.FILL, DEF_LOW_COLOR, DEF_MID_COLOR, DEF_HIGH_COLOR)
    )

    internal class LayerParams {
        var added: Boolean = false
            private set
        var type: String? = null
            set(v) {
                added = true
                field = v
            }
        var diag: Boolean? = null
            set(v) {
                added = true
                field = v
            }
        var color: String? = null
            set(v) {
                added = true
                field = v
            }
        var mapSize: Boolean? = null
            set(v) {
                added = true
                field = v
            }
    }

    fun tiles(type: String? = null, diag: Boolean? = null): CorrPlot {
        checkTypeArg(type)
        tiles.type = type
        tiles.diag = diag
        return this
    }

    fun points(type: String? = null, diag: Boolean? = null): CorrPlot {
        checkTypeArg(type)
        points.type = type
        points.diag = diag
        return this
    }

    fun labels(type: String? = null, diag: Boolean? = null, mapSize: Boolean? = null, color: String? = null): CorrPlot {
        checkTypeArg(type)
        labels.type = type
        labels.diag = diag
        labels.mapSize = mapSize
        labels.color = color

        return this
    }

    fun brewerPalette(palette: String): CorrPlot {
        colorScaleOptions = scaleBrewer(Aes.COLOR, palette)
        fillScaleOptions = scaleBrewer(Aes.FILL, palette)
        return this
    }

    fun gradientPalette(low: String, mid: String, high: String): CorrPlot {
        colorScaleOptions = scaleGradient(Aes.COLOR, low, mid, high)
        fillScaleOptions = scaleGradient(Aes.FILL, low, mid, high)
        return this
    }

    /**
     * Use Brewer 'BrBG' colors
     */
    fun paletteBrBG() = brewerPalette("BrBG")

    /**
     * Use Brewer 'PiYG' colors
     */
    fun palettePiYG() = brewerPalette("PiYG")

    /**
     * Use Brewer 'PRGn' colors
     */
    fun palettePRGn() = brewerPalette("PRGn")

    /**
     * Use Brewer 'PuOr' colors
     */
    fun palettePuOr() = brewerPalette("PuOr")

    /**
     * Use Brewer 'RdBu' colors
     */
    fun paletteRdBu() = brewerPalette("RdBu")

    /**
     * Use Brewer 'RdGy' colors
     */
    fun paletteRdGy() = brewerPalette("RdGy")

    /**
     * Use Brewer 'RdYlBu' colors
     */
    fun paletteRdYlBu() = brewerPalette("RdYlBu")

    /**
     * Use Brewer 'RdYlGn' colors
     */
    fun paletteRdYlGn() = brewerPalette("RdYlGn")

    /**
     * Use Brewer 'Spectral' colors
     */
    fun paletteSpectral() = brewerPalette("Spectral")

    fun build(): PlotOptions {
        if (!(tiles.added || points.added || labels.added)) {
            return PlotOptions()
        }

        OptionsConfigurator.configure(tiles, points, labels, flip)

        val originalVariables = data.keys.map { it.toString() }.toList()

        // Compute correlations
        @Suppress("NAME_SHADOWING")
        val data = standardiseData(data)
        val correlations = correlations(data, ::correlationPearson)
        // variables in the 'original' order
        val varsInMatrix = correlations.keys.map { it.first }.toSet()
        val varsInOrder = originalVariables.filter { varsInMatrix.contains(it) }

        val keepDiag = getKeepMatrixDiag(tiles, points, labels)
        val combinedType = OptionsConfigurator.getCombinedMatrixType(tiles, points, labels)

        val layers = mutableListOf<LayerOptions>()

        // Add layers
        val tooltipsOptions = TooltipsOptions().apply {
            lines = listOf(variable(CorrVar.CORR))
            formats = listOf(
                Format().apply {
                    field = variable(CorrVar.CORR)
                    format = VALUE_FORMAT
                }
            )
        }

        if (tiles.added) {
            layers.add(
                LayerOptions().apply {
                    this.geom = GeomKind.TILE
                    this.data = layerData(
                        tiles,
                        correlations,
                        varsInOrder,
                        keepDiag = keepDiag || combinedType == FULL,
                        threshold
                    )
                    this.mappings = mapOf(
                        Aes.X to CorrVar.X,
                        Aes.Y to CorrVar.Y,
                        Aes.FILL to CorrVar.CORR,
                    )
                    this[Aes.SIZE] = 0.0
                    this[Aes.WIDTH] = 1.002
                    this[Aes.HEIGHT] = 1.002
                    this.showLegend = showLegend
                    this.tooltipsOptions = tooltipsOptions
                    this.samplingOptions = SamplingOptions.NONE
                    this.naText = ""
                    this.labelFormat = VALUE_FORMAT
                })
        }

        if (points.added) {
            layers.add(
                LayerOptions().apply {
                    this.geom = GeomKind.POINT
                    this.data = layerData(
                        points,
                        correlations,
                        varsInOrder,
                        keepDiag = keepDiag || combinedType == FULL,
                        threshold
                    )
                    this.mappings = mapOf(
                        Aes.X to CorrVar.X,
                        Aes.Y to CorrVar.Y,
                        Aes.SIZE to CorrVar.CORR_ABS,
                        Aes.COLOR to CorrVar.CORR,
                    )
                    this.sizeUnit = Aes.X
                    this.showLegend = showLegend
                    this.tooltipsOptions = tooltipsOptions
                    this.samplingOptions = SamplingOptions.NONE
                    this.naText = ""
                    this.labelFormat = VALUE_FORMAT
                })
        }

        if (labels.added) {
            val layerData = layerData(
                labels,
                correlations,
                varsInOrder,
                keepDiag = keepDiag || combinedType == FULL,
                threshold
            )
            layers.add(
                LayerOptions().apply {
                    this.geom = GeomKind.TEXT
                    this.data = layerData
                    this.mappings = mapOf(
                        Aes.X to CorrVar.X,
                        Aes.Y to CorrVar.Y,
                        Aes.LABEL to CorrVar.CORR,
                        Aes.SIZE to CorrVar.CORR_ABS,
                        Aes.COLOR to CorrVar.CORR
                    )
                    this.showLegend = showLegend
                    this.naText = ""
                    this.labelFormat = VALUE_FORMAT
                    this.sizeUnit = Aes.X
                    this.tooltipsOptions = tooltipsOptions
                    this.samplingOptions = SamplingOptions.NONE
                    this.size = if (labels.mapSize == true) null else 1.0
                    this.color = labels.color
                })
        }

        // Actual labels on axis.
        val (xs, ys) = matrixXYSeries(
            correlations, varsInOrder, combinedType, !keepDiag, threshold,
            dropDiagNA = !keepDiag,
            dropOtherNA = combinedType == FULL
        )

        val plotOptions = PlotOptions()
        plotOptions.size = plotSize(xs, ys, title != null, showLegend, adjustSize)
        plotOptions.title = title
        plotOptions.layerOptions = layers

        // preserve the original order on x/y scales
        val xsSet = xs.distinct().toSet()
        val ysSet = ys.distinct().toSet()
        val plotX = varsInOrder.filter { it in xsSet }
        val plotY = varsInOrder.filter { it in ysSet }

        val onlyTiles = tiles.added && !points.added && !labels.added

        return addCommonParams(plotOptions, plotX, plotY, onlyTiles, flip)
    }

    private fun addCommonParams(
        plotOptions: PlotOptions,
        xValues: List<String>,
        yValues: List<String>,
        onlyTiles: Boolean,
        flipY: Boolean
    ): PlotOptions {
        @Suppress("NAME_SHADOWING")
        plotOptions.themeOptions = ThemeOptions().apply {
            axisTitle = ThemeOptions.BLANK
            axisLine = ThemeOptions.BLANK
        }

        val scaleSize =  ScaleOptions().apply {
            aes = Aes.SIZE
            mapperKind = ScaleConfig.IDENTITY
            naValue = 0
            guide = Option.Guide.NONE
        }

        val scaleX = ScaleOptions().apply {
            aes = Aes.X
            isDiscrete = true
            breaks = xValues
            limits = xValues
            expand = EXPAND
        }

        val scaleY = ScaleOptions().apply {
            aes = Aes.Y
            isDiscrete = true
            breaks = yValues
            limits = if (flipY) yValues.asReversed() else yValues
            expand = EXPAND
        }

        plotOptions.scaleOptions = listOf(scaleSize, scaleX, scaleY, colorScaleOptions, fillScaleOptions)

        val xLim = Pair(-0.6, xValues.size - 1 + 0.6)
        val yLim = Pair(-0.6, yValues.size - 1 + 0.6)
        plotOptions.coord = when (onlyTiles) {
            true -> CoordOptions().apply {
                this.name = Option.CoordName.CARTESIAN
                this.xLim = xLim
                this.yLim = yLim
            }
            false -> CoordOptions().apply {
                this.name = Option.CoordName.FIXED
                this.xLim = xLim
                this.yLim = yLim
            }
        }

        return plotOptions
    }

    companion object {
        private const val VALUE_FORMAT = ".2f"

        private const val LEGEND_NAME = "Corr"
        private val SCALE_BREAKS = listOf(-1.0, -0.5, 0.0, 0.5, 1.0)
        private val SCALE_LABELS = listOf("-1", "-0.5", "0", "0.5", "1")
        private val SCALE_LIMITS = listOf(-1.0, 1.0)

        private const val DEF_THRESHOLD = 0.0
        private const val DEF_LOW_COLOR = "#B3412C" //"red"
        private const val DEF_MID_COLOR = "#EDEDED" //"light_gray"
        private const val DEF_HIGH_COLOR = "#326C81" // "blue"
        private const val NA_VALUE_COLOR = "rgba(0,0,0,0)"
        private val EXPAND = listOf(0.0, 0.0)

        private const val COLUMN_WIDTH = 40
        private const val MIN_PLOT_WIDTH = 150
        private const val MAX_PLOT_WIDTH = 700
//        private const val PLOT_PROPORTION = 3.0 / 4.0

        private fun checkTypeArg(type: String?) {
            type?.run {
                require(type in listOf(UPPER, LOWER, FULL)) {
                    """The option 'type' must be "$UPPER", "$LOWER" or "$FULL" but was: "$type""""
                }
            }
        }

        private fun scaleGradient(aesthetic: Aes<*>, low: String, mid: String, high: String): ScaleOptions {
            return ScaleOptions().apply {
                aes = aesthetic
                name = LEGEND_NAME
                breaks = SCALE_BREAKS
                limits = SCALE_LIMITS
                naValue = NA_VALUE_COLOR
                mapperKind = COLOR_GRADIENT2
                this.low = low
                this.mid = mid
                this.high = high
            }
        }

        private fun scaleBrewer(aesthetic: Aes<*>, paletteName: String): ScaleOptions {
            return ScaleOptions().apply {
                aes = aesthetic
                mapperKind = COLOR_BREWER
                palette = paletteName
                labels = SCALE_LABELS
                name = LEGEND_NAME
                breaks = SCALE_BREAKS
                limits = SCALE_LIMITS
                naValue = NA_VALUE_COLOR
            }
        }


        private fun plotSize(
            xs: List<String>,
            ys: List<String>,
            hasTitle: Boolean,
            hasLegend: Boolean,
            adjustSize: Double
        ): PlotOptions.Size {
            val colCount = xs.distinct().size

            // magic values
            val titleHeight = if (hasTitle) 20 else 0
            val legendWidth = if (hasLegend) 70 else 0
            val geomWidth = (min(MAX_PLOT_WIDTH, max(MIN_PLOT_WIDTH, (colCount * COLUMN_WIDTH))) * adjustSize).toInt()

            fun axisLabelWidth(labs: List<String>): Int {
                val labelLen = labs.maxByOrNull { it.length }?.length ?: 0
                return (labelLen * 5.7).toInt()
            }

            val labelWidthX = axisLabelWidth(xs)
            val labelWidthY = axisLabelWidth(ys)
            val colWidth = geomWidth / colCount
            val labelHeightY = if (labelWidthY * 1.0 > colWidth) labelWidthY / 2 else 20

            val width = geomWidth + labelWidthX + legendWidth
            val height = geomWidth + titleHeight + labelHeightY

            return PlotOptions.Size().apply {
                this.width = width
                this.height = height
            }
        }

        private fun layerData(
            params: LayerParams,
            correlations: Map<Pair<String, String>, Double>,
            varsInOrder: List<String>,
            keepDiag: Boolean,
            threshold: Double
        ): Map<String, List<Any?>> {
            val diag = params.diag!!
            val type = params.type!!

            val (xs, ys) = matrixXYSeries(
                correlations, varsInOrder, type,
                nullDiag = !(keepDiag),
                threshold,
                dropDiagNA = false,
                dropOtherNA = false
            )

            val matrix = CorrUtil.CorrMatrix(
                correlations,
                nullDiag = !diag,
                threshold
            )

            return correlationsToDataframe(matrix, xs, ys)
        }
    }
}
