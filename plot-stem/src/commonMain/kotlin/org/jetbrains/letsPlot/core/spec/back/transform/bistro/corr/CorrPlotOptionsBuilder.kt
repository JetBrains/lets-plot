/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_BREWER
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_GRADIENT2
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.CorrUtil.computeCorrelations
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.CorrUtil.correlationsFromCoefficients
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.CorrUtil.correlationsToDataframe
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.CorrUtil.matrixXYSeries
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.Method.correlationPearson
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.Option.Corr.Layer.Type.FULL
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.Option.Corr.Layer.Type.LOWER
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.Option.Corr.Layer.Type.UPPER
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.OptionsConfigurator.getKeepMatrixDiag
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.DataUtil.standardiseData
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.ThemeOptions.Element
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
 * @param flip Whether to flip the y-axis.
 * @param threshold Minimal correlation abs value to be included in result. Must be in interval [0.0, 1.0]
 * @param adjustSize A scaler to adjust the plot size which was computed by `CorrPlot` automatically.
 */
class CorrPlotOptionsBuilder private constructor(
    private val data: Map<*, *>,
    private val coefficients: Boolean,
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
        coefficients: Boolean? = null,
        title: String? = null,
        showLegend: Boolean? = null,
        flip: Boolean? = null,
        threshold: Double? = null,
        adjustSize: Double? = null,
    ) : this(
        data,
        coefficients ?: false,
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as LayerParams

            if (added != other.added) return false
            if (type != other.type) return false
            if (diag != other.diag) return false
            if (color != other.color) return false
            if (mapSize != other.mapSize) return false

            return true
        }

        override fun hashCode(): Int {
            var result = added.hashCode()
            result = 31 * result + (type?.hashCode() ?: 0)
            result = 31 * result + (diag?.hashCode() ?: 0)
            result = 31 * result + (color?.hashCode() ?: 0)
            result = 31 * result + (mapSize?.hashCode() ?: 0)
            return result
        }
    }

    fun tiles(type: String? = null, diag: Boolean? = null): CorrPlotOptionsBuilder {
        checkTypeArg(type)
        tiles.type = type
        tiles.diag = diag
        return this
    }

    fun points(type: String? = null, diag: Boolean? = null): CorrPlotOptionsBuilder {
        checkTypeArg(type)
        points.type = type
        points.diag = diag
        return this
    }

    fun labels(
        type: String? = null,
        diag: Boolean? = null,
        mapSize: Boolean? = null,
        color: String? = null
    ): CorrPlotOptionsBuilder {
        checkTypeArg(type)
        labels.type = type
        labels.diag = diag
        labels.mapSize = mapSize
        labels.color = color

        return this
    }

    fun brewerPalette(palette: String): CorrPlotOptionsBuilder {
        colorScaleOptions = scaleBrewer(Aes.COLOR, palette)
        fillScaleOptions = scaleBrewer(Aes.FILL, palette)
        return this
    }

    fun gradientPalette(low: String, mid: String, high: String): CorrPlotOptionsBuilder {
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

        val data = standardiseData(data)
        val correlations = when (coefficients) {
            true -> correlationsFromCoefficients(data)
            false -> computeCorrelations(data, ::correlationPearson)
        }

        // variables in the 'original' order
        val varsInMatrix = correlations.keys.map { it.first }.toSet()
        val varsInOrder = data.keys.filter(varsInMatrix::contains)

        val keepDiag = getKeepMatrixDiag(tiles, points, labels)
        val combinedType = OptionsConfigurator.getCombinedMatrixType(tiles, points, labels)

        val layers = mutableListOf<LayerOptions>()

        // Add layers
        if (tiles.added) {
            layers.add(
                newCorrPlotLayerOptions {
                    geom = GeomKind.TILE
                    this.data = layerData(
                        tiles,
                        correlations,
                        varsInOrder,
                        keepDiag = keepDiag || combinedType == FULL,
                        threshold
                    )
                    mapping = Mapping(
                        Aes.X to CorrVar.X,
                        Aes.Y to CorrVar.Y,
                        Aes.FILL to CorrVar.CORR,
                    )
                    size = 0.0
                    width = 1.002
                    height = 1.002
                }
            )
        }

        if (points.added) {
            layers.add(
                newCorrPlotLayerOptions {
                    geom = GeomKind.POINT
                    prop[PointLayer.SIZE_UNIT] = Aes.X
                    this.data = layerData(
                        points,
                        correlations,
                        varsInOrder,
                        keepDiag = keepDiag || combinedType == FULL,
                        threshold
                    )
                    mapping = Mapping(
                        Aes.X to CorrVar.X,
                        Aes.Y to CorrVar.Y,
                        Aes.SIZE to CorrVar.CORR_ABS,
                        Aes.COLOR to CorrVar.CORR,
                    )
                }
            )
        }

        if (labels.added) {
            layers.add(
                newCorrPlotLayerOptions {
                    geom = GeomKind.TEXT
                    prop[TextLayer.NA_TEXT] = ""
                    prop[TextLayer.LABEL_FORMAT] = VALUE_FORMAT
                    prop[TextLayer.SIZE_UNIT] = Aes.X
                    this.data = layerData(
                        labels,
                        correlations,
                        varsInOrder,
                        keepDiag = keepDiag || combinedType == FULL,
                        threshold
                    )
                    mapping = Mapping(
                        Aes.X to CorrVar.X,
                        Aes.Y to CorrVar.Y,
                        Aes.LABEL to CorrVar.CORR,
                        Aes.SIZE to CorrVar.CORR_ABS,
                        Aes.COLOR to CorrVar.CORR
                    )
                    size = if (labels.mapSize == true) null else 1.0
                    color = labels.color
                }
            )
        }

        // Actual labels on axis.
        val (xs, ys) = matrixXYSeries(
            correlations, varsInOrder, combinedType, !keepDiag, threshold,
            dropDiagNA = !keepDiag,
            dropOtherNA = combinedType == FULL
        )


        // preserve the original order on x/y scales
        val xsSet = xs.distinct().toSet()
        val ysSet = ys.distinct().toSet()
        val plotX = varsInOrder.filter { it in xsSet }
        val plotY = varsInOrder.filter { it in ysSet }

        return plot {
            size = plotSize(xs, ys, title != null, showLegend, adjustSize)
            title = title
            layerOptions = layers
            themeOptions = theme {
                axisTitle = Element.BLANK
                axisLine = Element.BLANK
                panelGrid = Element.BLANK
                axisTicksX = Element.line()
                axisTicksY = Element.line()
            }
            scaleOptions = listOf(
                scale {
                    aes = Aes.SIZE
                    mapperKind = MapperKind.IDENTITY
                    naValue = 0
                    guide = GuideOptions.none()
                },
                scale {
                    aes = Aes.X
                    isDiscrete = true
                    breaks = plotX
                    limits = plotX
                    expand = EXPAND
                },
                scale {
                    aes = Aes.Y
                    isDiscrete = true
                    breaks = plotY
                    limits = if (flip) plotY.asReversed() else plotY
                    expand = EXPAND
                },
                colorScaleOptions,
                fillScaleOptions
            )
            coord = coord {
                name = CoordOptions.CoordName.FIXED
                xLim = Pair(-0.6, plotX.size - 1 + 0.6)
                yLim = Pair(-0.6, plotY.size - 1 + 0.6)
            }
        }
    }

    private fun newCorrPlotLayerOptions(block: LayerOptions.() -> Unit): LayerOptions {
        return LayerOptions().apply {
            showLegend = this@CorrPlotOptionsBuilder.showLegend
            tooltipsOptions = tooltips {
                lines = listOf(TooltipsOptions.variable(CorrVar.CORR))
                formats = listOf(
                    TooltipsOptions.format {
                        field = TooltipsOptions.variable(CorrVar.CORR)
                        format = VALUE_FORMAT
                    }
                )
            }
            samplingOptions = SamplingOptions.NONE
            block(this)
        }
    }

    companion object {
        private const val VALUE_FORMAT = ".2f"

        private const val LEGEND_NAME = ""
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
            return scale {
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
            return scale {
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
            val colWidth = when (colCount) {
                0 -> geomWidth
                else -> geomWidth / colCount
            }

            val labelHeightY = if (labelWidthY * 1.0 > colWidth) labelWidthY / 2 else 20

            val width = geomWidth + labelWidthX + legendWidth
            val height = geomWidth + titleHeight + labelHeightY

            return PlotOptions.size {
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
