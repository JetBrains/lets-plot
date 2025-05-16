/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.geom.*
import org.jetbrains.letsPlot.core.plot.base.geom.repel.LabelForceLayout
import org.jetbrains.letsPlot.core.plot.base.geom.util.LabelOptions
import org.jetbrains.letsPlot.core.plot.base.stat.DotplotStat
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
import org.jetbrains.letsPlot.core.spec.Option.Geom.Pie
import org.jetbrains.letsPlot.core.spec.Option.Geom.Spoke
import org.jetbrains.letsPlot.core.spec.config.ArrowSpecConfig
import org.jetbrains.letsPlot.core.spec.config.LayerConfig
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion

internal object GeomProviderFactory {
    private val PROVIDER = HashMap<GeomKind, GeomProvider>()

    // Simple provides
    init {
        PROVIDER[GeomKind.BLANK] = GeomProvider.blank()
        PROVIDER[GeomKind.LINE] = GeomProvider.line()
        PROVIDER[GeomKind.SMOOTH] = GeomProvider.smooth()
        PROVIDER[GeomKind.BAR] = GeomProvider.bar()
        PROVIDER[GeomKind.HISTOGRAM] = GeomProvider.histogram()
        PROVIDER[GeomKind.RIBBON] = GeomProvider.ribbon()
        PROVIDER[GeomKind.LINE_RANGE] = GeomProvider.lineRange()
        PROVIDER[GeomKind.BIN_2D] = GeomProvider.bin2d()
        PROVIDER[GeomKind.CONTOUR] = GeomProvider.contour()
        PROVIDER[GeomKind.CONTOURF] = GeomProvider.contourf()
        PROVIDER[GeomKind.POLYGON] = GeomProvider.polygon()
        PROVIDER[GeomKind.MAP] = GeomProvider.map()
        PROVIDER[GeomKind.AB_LINE] = GeomProvider.abline()
        PROVIDER[GeomKind.H_LINE] = GeomProvider.hline()
        PROVIDER[GeomKind.V_LINE] = GeomProvider.vline()
        PROVIDER[GeomKind.DENSITY2D] = GeomProvider.density2d()
        PROVIDER[GeomKind.DENSITY2DF] = GeomProvider.density2df()
        PROVIDER[GeomKind.JITTER] = GeomProvider.jitter()
        PROVIDER[GeomKind.Q_Q] = GeomProvider.qq()
        PROVIDER[GeomKind.Q_Q_2] = GeomProvider.qq2()
        PROVIDER[GeomKind.Q_Q_LINE] = GeomProvider.qqline()
        PROVIDER[GeomKind.Q_Q_2_LINE] = GeomProvider.qq2line()
        PROVIDER[GeomKind.FREQPOLY] = GeomProvider.freqpoly()
        PROVIDER[GeomKind.RECT] = GeomProvider.rect()
        PROVIDER[GeomKind.RASTER] = GeomProvider.raster()
        PROVIDER[GeomKind.LIVE_MAP] = GeomProvider.livemap()
    }

    fun createGeomProvider(
        geomKind: GeomKind,
        layerConfig: LayerConfig,
        aopConversion: AesOptionConversion,
        expFormat: ExponentFormat
    ): GeomProvider {
        return when (geomKind) {
            GeomKind.AREA -> GeomProvider.area {
                val geom = AreaGeom()

                if (layerConfig.hasOwn(Option.Geom.Area.FLAT)) {
                    geom.flat = layerConfig.getBoolean(Option.Geom.Area.FLAT)
                }
                geom
            }

            GeomKind.DENSITY -> GeomProvider.density {
                val geom = DensityGeom()
                if (layerConfig.hasOwn(Option.Stat.Density.QUANTILES)) {
                    geom.quantiles = layerConfig.getBoundedDoubleList(Option.Stat.Density.QUANTILES, 0.0, 1.0)
                }
                if (layerConfig.hasOwn(Option.Geom.Density.QUANTILE_LINES)) {
                    geom.quantileLines =
                        layerConfig.getBoolean(Option.Geom.Density.QUANTILE_LINES, DensityGeom.DEF_QUANTILE_LINES)
                }
                geom
            }

            GeomKind.DOT_PLOT -> GeomProvider.dotplot {
                val geom = DotplotGeom()
                if (layerConfig.hasOwn(Option.Geom.Dotplot.DOTSIZE)) {
                    geom.dotSize = layerConfig.getDouble(Option.Geom.Dotplot.DOTSIZE)!!
                }
                if (layerConfig.hasOwn(Option.Geom.Dotplot.STACKRATIO)) {
                    geom.stackRatio = layerConfig.getDouble(Option.Geom.Dotplot.STACKRATIO)!!
                }
                if (layerConfig.hasOwn(Option.Geom.Dotplot.STACKGROUPS)) {
                    geom.stackGroups = layerConfig.getBoolean(Option.Geom.Dotplot.STACKGROUPS)
                }
                if (layerConfig.hasOwn(Option.Geom.Dotplot.STACKDIR)) {
                    geom.stackDir = layerConfig.getString(Option.Geom.Dotplot.STACKDIR)!!.let {
                        when (it.lowercase()) {
                            "up" -> DotplotGeom.Stackdir.UP
                            "down" -> DotplotGeom.Stackdir.DOWN
                            "center" -> DotplotGeom.Stackdir.CENTER
                            "centerwhole" -> DotplotGeom.Stackdir.CENTERWHOLE
                            else -> throw IllegalArgumentException(
                                "Unsupported ${Option.Geom.Dotplot.STACKDIR}: '$it'. " +
                                        "Use one of: up, down, center, centerwhole."
                            )
                        }
                    }
                }
                if (layerConfig.hasOwn(Option.Geom.Dotplot.METHOD)) {
                    geom.method = DotplotStat.Method.safeValueOf(layerConfig.getString(Option.Geom.Dotplot.METHOD)!!)
                }
                geom
            }

            GeomKind.TILE -> GeomProvider.tile {
                TileGeom().apply {
                    this.widthUnit = dimensionUnit(layerConfig, Option.Geom.Tile.WIDTH_UNIT) ?: TileGeom.DEF_WIDTH_UNIT
                    this.heightUnit = dimensionUnit(layerConfig, Option.Geom.Tile.HEIGHT_UNIT) ?: TileGeom.DEF_HEIGHT_UNIT
                }
            }

            GeomKind.HEX -> GeomProvider.hex {
                HexGeom().apply {
                    this.widthUnit = dimensionUnit(layerConfig, Option.Geom.Hex.WIDTH_UNIT) ?: HexGeom.DEF_WIDTH_UNIT
                    this.heightUnit = dimensionUnit(layerConfig, Option.Geom.Hex.HEIGHT_UNIT) ?: HexGeom.DEF_HEIGHT_UNIT
                }
            }

            GeomKind.ERROR_BAR -> GeomProvider.errorBar { ctx ->
                ErrorBarGeom().apply {
                    if (layerConfig.hasOwn(Option.Geom.ErrorBar.WIDTH_UNIT)) {
                        this.widthUnit = dimensionUnit(layerConfig, Option.Geom.ErrorBar.WIDTH_UNIT)!!
                    }
                }
            }

            GeomKind.CROSS_BAR -> GeomProvider.crossBar { ctx ->
                val geom = CrossBarGeom()
                if (layerConfig.hasOwn(Option.Geom.CrossBar.FATTEN)) {
                    geom.fattenMidline = layerConfig.getDouble(Option.Geom.CrossBar.FATTEN)!!
                }
                val isVertical = isVertical(ctx, geomKind.name)
                val midlineAes = if (isVertical) Aes.Y else Aes.X
                val midlineIsMapped = ctx.hasBinding(midlineAes) || ctx.hasConstant(midlineAes)
                if (!midlineIsMapped) {
                    geom.fattenMidline = 0.0 // The `fattenMidline` variable affects the legend: if set to 0, the midline is omitted.
                }
                if (layerConfig.hasOwn(Option.Geom.CrossBar.WIDTH_UNIT)) {
                    geom.widthUnit = dimensionUnit(layerConfig, Option.Geom.CrossBar.WIDTH_UNIT)!!
                }
                geom
            }

            GeomKind.POINT_RANGE -> GeomProvider.pointRange { ctx ->
                val geom = PointRangeGeom()
                if (layerConfig.hasOwn(Option.Geom.PointRange.FATTEN)) {
                    geom.fattenMidPoint = layerConfig.getDouble(Option.Geom.PointRange.FATTEN)!!
                }
                geom
            }

            GeomKind.BAND -> GeomProvider.band { ctx ->
                BandGeom(isVertical(ctx, geomKind.name))
            }

            GeomKind.BOX_PLOT -> GeomProvider.boxplot { ctx ->
                val geom = BoxplotGeom()
                if (layerConfig.hasOwn(Option.Geom.Boxplot.FATTEN)) {
                    geom.fattenMidline = layerConfig.getDouble(Option.Geom.Boxplot.FATTEN)!!
                }
                if (layerConfig.hasOwn(Option.Geom.Boxplot.WHISKER_WIDTH)) {
                    geom.whiskerWidth = layerConfig.getDouble(Option.Geom.Boxplot.WHISKER_WIDTH)!!
                }
                if (layerConfig.hasOwn(Option.Geom.Boxplot.WIDTH_UNIT)) {
                    geom.widthUnit = dimensionUnit(layerConfig, Option.Geom.Boxplot.WIDTH_UNIT)!!
                }
                geom
            }

            GeomKind.AREA_RIDGES -> GeomProvider.arearidges {
                val geom = AreaRidgesGeom()
                if (layerConfig.hasOwn(Option.Geom.AreaRidges.SCALE)) {
                    geom.scale = layerConfig.getDoubleDef(Option.Geom.AreaRidges.SCALE, AreaRidgesGeom.DEF_SCALE)
                }
                if (layerConfig.hasOwn(Option.Geom.AreaRidges.MIN_HEIGHT)) {
                    geom.minHeight =
                        layerConfig.getDoubleDef(Option.Geom.AreaRidges.MIN_HEIGHT, AreaRidgesGeom.DEF_MIN_HEIGHT)
                }
                if (layerConfig.hasOwn(Option.Stat.DensityRidges.QUANTILES)) {
                    geom.quantiles = layerConfig.getBoundedDoubleList(Option.Stat.DensityRidges.QUANTILES, 0.0, 1.0)
                }
                if (layerConfig.hasOwn(Option.Geom.AreaRidges.QUANTILE_LINES)) {
                    geom.quantileLines =
                        layerConfig.getBoolean(Option.Geom.AreaRidges.QUANTILE_LINES, AreaRidgesGeom.DEF_QUANTILE_LINES)
                }
                geom
            }

            GeomKind.VIOLIN -> GeomProvider.violin {
                val geom = ViolinGeom()
                if (layerConfig.hasOwn(Option.Stat.YDensity.QUANTILES)) {
                    geom.quantiles = layerConfig.getBoundedDoubleList(Option.Stat.YDensity.QUANTILES, 0.0, 1.0)
                }
                if (layerConfig.hasOwn(Option.Geom.Violin.QUANTILE_LINES)) {
                    geom.quantileLines =
                        layerConfig.getBoolean(Option.Geom.Violin.QUANTILE_LINES, ViolinGeom.DEF_QUANTILE_LINES)
                }
                if (layerConfig.hasOwn(Option.Geom.Violin.SHOW_HALF)) {
                    geom.showHalf = layerConfig.getDouble(Option.Geom.Violin.SHOW_HALF)!!
                }
                geom
            }

            GeomKind.SINA -> GeomProvider.sina {
                val geom = SinaGeom()
                if (layerConfig.hasOwn(Option.Geom.Sina.SEED)) {
                    geom.seed = layerConfig.getLong(Option.Geom.Sina.SEED)!!
                }
                if (layerConfig.hasOwn(Option.Stat.Sina.QUANTILES)) {
                    geom.quantiles = layerConfig.getBoundedDoubleList(Option.Stat.Sina.QUANTILES, 0.0, 1.0)
                }
                if (layerConfig.hasOwn(Option.Geom.Sina.SHOW_HALF)) {
                    geom.showHalf = layerConfig.getDouble(Option.Geom.Sina.SHOW_HALF)!!
                }
                geom
            }

            GeomKind.Y_DOT_PLOT -> GeomProvider.ydotplot {
                val geom = YDotplotGeom()
                if (layerConfig.hasOwn(Option.Geom.YDotplot.DOTSIZE)) {
                    geom.dotSize = layerConfig.getDouble(Option.Geom.YDotplot.DOTSIZE)!!
                }
                if (layerConfig.hasOwn(Option.Geom.YDotplot.STACKRATIO)) {
                    geom.stackRatio = layerConfig.getDouble(Option.Geom.YDotplot.STACKRATIO)!!
                }
                if (layerConfig.hasOwn(Option.Geom.YDotplot.STACKGROUPS)) {
                    geom.stackGroups = layerConfig.getBoolean(Option.Geom.YDotplot.STACKGROUPS)
                }
                if (layerConfig.hasOwn(Option.Geom.YDotplot.STACKDIR)) {
                    geom.yStackDir = layerConfig.getString(Option.Geom.YDotplot.STACKDIR)!!.let {
                        when (it.lowercase()) {
                            "left" -> YDotplotGeom.YStackdir.LEFT
                            "right" -> YDotplotGeom.YStackdir.RIGHT
                            "center" -> YDotplotGeom.YStackdir.CENTER
                            "centerwhole" -> YDotplotGeom.YStackdir.CENTERWHOLE
                            else -> throw IllegalArgumentException(
                                "Unsupported ${Option.Geom.YDotplot.STACKDIR}: '$it'. " +
                                        "Use one of: left, right, center, centerwhole."
                            )
                        }
                    }
                }
                if (layerConfig.hasOwn(Option.Geom.YDotplot.METHOD)) {
                    geom.method = DotplotStat.Method.safeValueOf(layerConfig.getString(Option.Geom.YDotplot.METHOD)!!)
                }
                geom
            }

            GeomKind.STEP -> GeomProvider.step {
                val geom = StepGeom()
                if (layerConfig.hasOwn(Option.Geom.Step.DIRECTION)) {
                    geom.setDirection(layerConfig.getString(Option.Geom.Step.DIRECTION)!!)
                }
                if (layerConfig.hasOwn(Option.Geom.Step.PADDED)) {
                    geom.padded = layerConfig.getBoolean(Option.Geom.Step.PADDED, StepGeom.DEF_PADDED)
                }
                geom
            }

            GeomKind.SEGMENT -> GeomProvider.segment {
                val geom = SegmentGeom()
                if (layerConfig.has(Option.Geom.Segment.ARROW)) {
                    val arrowConfig = ArrowSpecConfig.create(layerConfig[Option.Geom.Segment.ARROW]!!)
                    geom.arrowSpec = arrowConfig.createArrowSpec()
                }
                if (layerConfig.has(Option.Geom.Segment.ANIMATION)) {
                    geom.animation = layerConfig[Option.Geom.Segment.ANIMATION]
                }
                if (layerConfig.has(Option.Geom.Segment.FLAT)) {
                    geom.flat = layerConfig.getBoolean(Option.Geom.Segment.FLAT)
                }
                if (layerConfig.has(Option.Geom.Segment.GEODESIC)) {
                    geom.geodesic = layerConfig.getBoolean(Option.Geom.Segment.GEODESIC)
                }
                layerConfig.getDouble(Option.Geom.Segment.SPACER)?.let { geom.spacer = it }
                geom
            }

            GeomKind.CURVE -> return GeomProvider.curve {
                val geom = CurveGeom()
                layerConfig.getDouble(Option.Geom.Curve.CURVATURE)?.let { geom.curvature = it }
                layerConfig.getDouble(Option.Geom.Curve.ANGLE)?.let { geom.angle = it }
                layerConfig.getInteger(Option.Geom.Curve.NCP)?.let { geom.ncp = it }
                if (layerConfig.has(Option.Geom.Segment.ARROW)) {
                    val arrowConfig = ArrowSpecConfig.create(layerConfig[Option.Geom.Curve.ARROW]!!)
                    geom.arrowSpec = arrowConfig.createArrowSpec()
                }
                layerConfig.getDouble(Option.Geom.Segment.SPACER)?.let { geom.spacer = it }
                geom
            }

            GeomKind.PATH -> GeomProvider.path {
                val geom = PathGeom()
                if (layerConfig.has(Option.Geom.Path.ANIMATION)) {
                    geom.animation = layerConfig[Option.Geom.Path.ANIMATION]
                }
                if (layerConfig.has(Option.Geom.Path.FLAT)) {
                    geom.flat = layerConfig.getBoolean(Option.Geom.Path.FLAT)
                }
                if (layerConfig.has(Option.Geom.Segment.GEODESIC)) {
                    geom.geodesic = layerConfig.getBoolean(Option.Geom.Segment.GEODESIC)
                }
                geom
            }

            GeomKind.POINT -> GeomProvider.point {
                val geom = PointGeom()

                if (layerConfig.has(Option.Geom.Point.ANIMATION)) {
                    geom.animation = layerConfig[Option.Geom.Point.ANIMATION]
                }

                geom.sizeUnit = layerConfig.getString(Option.Geom.Point.SIZE_UNIT)?.lowercase()
                geom
            }

            GeomKind.TEXT -> GeomProvider.text {
                val geom = TextGeom()
                applyTextOptions(layerConfig, geom, expFormat)
                geom
            }

            GeomKind.LABEL -> GeomProvider.label {
                val geom = LabelGeom()

                applyTextOptions(layerConfig, geom, expFormat)
                applyLabelOptions(layerConfig, geom.labelOptions)

                geom
            }

            GeomKind.TEXT_REPEL -> GeomProvider.textRepel {
                val geom = TextRepelGeom()
                applyTextOptions(layerConfig, geom, expFormat)
                applyRepelOptions(layerConfig, geom)
                geom
            }

            GeomKind.LABEL_REPEL -> GeomProvider.labelRepel {
                val geom = LabelRepelGeom()
                applyTextOptions(layerConfig, geom, expFormat)
                applyLabelOptions(layerConfig, geom.labelOptions)
                applyRepelOptions(layerConfig, geom)
                geom
            }

            GeomKind.IMAGE -> GeomProvider.image {
                require(layerConfig.hasOwn(Option.Geom.Image.HREF)) { "Image reference URL (href) is not specified." }
                for (s in listOf(
                    Option.Geom.Image.XMIN,
                    Option.Geom.Image.XMAX,
                    Option.Geom.Image.YMIN,
                    Option.Geom.Image.YMAX
                )) {
                    require(layerConfig.hasOwn(s)) { "'$s' is not specified." }
                }
                ImageGeom(
                    imageUrl = layerConfig.getString(Option.Geom.Image.HREF)!!,
                    bbox = DoubleRectangle.span(
                        DoubleVector(
                            layerConfig.getDoubleSafe(Option.Geom.Image.XMIN),
                            layerConfig.getDoubleSafe(Option.Geom.Image.YMIN)
                        ),
                        DoubleVector(
                            layerConfig.getDoubleSafe(Option.Geom.Image.XMAX),
                            layerConfig.getDoubleSafe(Option.Geom.Image.YMAX)
                        ),
                    )
                )
            }

            GeomKind.PIE -> GeomProvider.pie {
                val geom = PieGeom()
                layerConfig.getDouble(Pie.HOLE)?.let { geom.holeSize = it }
                layerConfig.getDouble(Pie.SPACER_WIDTH)?.let { geom.spacerWidth = it }
                layerConfig.getColor(Pie.SPACER_COLOR, aopConversion)?.let { geom.spacerColor = it }
                layerConfig.getString(Pie.STROKE_SIDE)?.let {
                    geom.strokeSide = when (it.lowercase()) {
                        "outer" -> PieGeom.StrokeSide.OUTER
                        "inner" -> PieGeom.StrokeSide.INNER
                        "both" -> PieGeom.StrokeSide.BOTH
                        else -> throw IllegalArgumentException(
                            "Unsupported value for ${Pie.STROKE_SIDE} parameter: '$it'. " +
                                    "Use one of: outer, inner, both."
                        )
                    }
                }
                geom.sizeUnit = layerConfig.getString(Pie.SIZE_UNIT)?.lowercase()
                geom.start = layerConfig.getDouble(Pie.START)
                geom.clockwise = (layerConfig.getInteger(Pie.DIRECTION) ?: 1)  == 1
                geom
            }

            GeomKind.LOLLIPOP -> GeomProvider.lollipop {
                val directionValue = layerConfig.getString(Option.Geom.Lollipop.DIRECTION)?.lowercase()
                val direction = directionValue?.let {
                    when (it) {
                        "v" -> LollipopGeom.Direction.ORTHOGONAL_TO_AXIS
                        "h" -> LollipopGeom.Direction.ALONG_AXIS
                        "s" -> LollipopGeom.Direction.SLOPE
                        else -> throw IllegalArgumentException(
                            "Unsupported value for ${Option.Geom.Lollipop.DIRECTION} parameter: '$it'. " +
                                    "Use one of: v, h, s."
                        )
                    }
                } ?: LollipopGeom.Direction.ORTHOGONAL_TO_AXIS
                val slope = layerConfig.getDouble(Option.Geom.Lollipop.SLOPE) ?: 0.0
                LollipopGeom().apply {
                    this.direction = direction
                    this.slope = slope
                    if (layerConfig.hasOwn(Option.Geom.Lollipop.INTERCEPT)) {
                        this.intercept = layerConfig.getDouble(Option.Geom.Lollipop.INTERCEPT)!!
                    }
                    if (layerConfig.hasOwn(Option.Geom.Lollipop.FATTEN)) {
                        this.fatten = layerConfig.getDouble(Option.Geom.Lollipop.FATTEN)!!
                    }
                }
            }

            GeomKind.SPOKE -> GeomProvider.spoke {
                val geom = SpokeGeom()
                layerConfig[Spoke.ARROW]?.let {
                    val arrowConfig = ArrowSpecConfig.create(it)
                    geom.arrowSpec = arrowConfig.createArrowSpec()
                }

                layerConfig.getString(Spoke.PIVOT)?.let {
                    geom.pivot = when (it.lowercase()) {
                        "tail" -> SpokeGeom.Pivot.TAIL
                        "middle", "mid" -> SpokeGeom.Pivot.MIDDLE
                        "tip" -> SpokeGeom.Pivot.TIP
                        else -> throw IllegalArgumentException(
                            "Unsupported value for ${Spoke.PIVOT} parameter: '$it'. " +
                                    "Use one of: tail, middle, mid, tip."
                        )
                    }
                }
                geom
            }

            else -> {
                require(PROVIDER.containsKey(geomKind)) { "Provider doesn't support geom kind: '$geomKind'" }
                PROVIDER.getValue(geomKind)
            }
        }
    }

    private fun applyTextOptions(layerConfig: LayerConfig, geom: TextGeom, expFormat: ExponentFormat) {
        layerConfig.getString(Option.Geom.Text.LABEL_FORMAT)?.let {
            geom.formatter = StringFormat.forOneArg(it, expFormat = PlotAssembler.extractExponentFormat(expFormat))::format
        }
        layerConfig.getString(Option.Geom.Text.NA_TEXT)?.let {
            geom.naValue = it
        }
        layerConfig.getString(Option.Geom.Text.SIZE_UNIT)?.let {
            geom.sizeUnit = it.lowercase()
        }
        layerConfig.getBoolean(Option.Geom.Text.CHECK_OVERLAP).let {
            geom.checkOverlap = it
        }
    }

    private fun applyLabelOptions(layerConfig: LayerConfig, factory: LabelOptions) {
        layerConfig.getDouble(Option.Geom.Label.LABEL_PADDING)?.let { factory.paddingFactor = it }
        layerConfig.getDouble(Option.Geom.Label.LABEL_R)?.let { factory.radiusFactor = it }
        layerConfig.getDouble(Option.Geom.Label.LABEL_SIZE)?.let { factory.borderWidth = it }
        if (layerConfig.hasOwn(Option.Geom.Label.ALPHA_STROKE)) {
            factory.alphaStroke = layerConfig.getBoolean(Option.Geom.Label.ALPHA_STROKE)
        }
    }

    private fun applyRepelOptions(layerConfig: LayerConfig, geom: TextRepelGeom) {
        layerConfig.getLong(Option.Geom.Repel.SEED)?.let {
            geom.seed = it
        }
        layerConfig.getInteger(Option.Geom.Repel.MAX_ITER)?.let {
            geom.maxIter = it
        }
        layerConfig.getDouble(Option.Geom.Repel.MAX_TIME)?.let {
            geom.maxTime = it * 1000.0 // convert to milliseconds
        }
        layerConfig.getString(Option.Geom.Repel.DIRECTION)?.let {
            geom.direction = directionValue(it)
        }
        layerConfig.getDouble(Option.Geom.Repel.POINT_PADDING)?.let {
            geom.pointPadding = it
        }
        layerConfig.getDouble(Option.Geom.Repel.BOX_PADDING)?.let {
            geom.boxPadding = it
        }
        layerConfig.getInteger(Option.Geom.Repel.MAX_OVERLAPS)?.let {
            geom.maxOverlaps = it
        }

        layerConfig.getDouble(Option.Geom.Repel.MIN_SEGMENT_LENGTH)?.let {
            geom.minSegmentLength = it
        }
    }

    // Should be removed when band geom will be refactored
    private fun isVertical(ctx: GeomProvider.Context, geomName: String): Boolean {
        // Horizontal or vertical
        val isVertical = setOf(Aes.YMIN, Aes.YMAX).any { aes -> ctx.hasBinding(aes) || ctx.hasConstant(aes) }
        val isHorizontal = setOf(Aes.XMIN, Aes.XMAX).any { aes -> ctx.hasBinding(aes) || ctx.hasConstant(aes) }
        require(!(isVertical && isHorizontal)) {
            "Either ymin, ymax or xmin, xmax must be specified for the $geomName."
        }
        return isVertical
    }

    private fun dimensionUnit(layerConfig: LayerConfig, option: String): DimensionUnit? {
        return layerConfig.getString(option)?.lowercase()?.let {
            when (it) {
                "res" -> DimensionUnit.RESOLUTION
                "identity" -> DimensionUnit.IDENTITY
                "size" -> DimensionUnit.SIZE
                "px" -> DimensionUnit.PIXEL
                else -> throw IllegalArgumentException(
                    "Unsupported value for $option parameter: '$it'. " +
                    "Use one of: res, identity, size, px."
                )
            }
        }
    }

    private fun directionValue(value: String): LabelForceLayout.Direction {
        return when (value.lowercase()) {
            "x" -> LabelForceLayout.Direction.X
            "y" -> LabelForceLayout.Direction.Y
            "both" -> LabelForceLayout.Direction.BOTH
            else -> throw IllegalArgumentException(
                "Unsupported value for ${Option.Geom.Repel.DIRECTION} parameter: '$value'. " +
                        "Use one of: x, y, both."
            )
        }
    }
}