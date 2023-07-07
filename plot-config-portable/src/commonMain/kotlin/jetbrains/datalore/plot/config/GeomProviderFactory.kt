/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.geom.*
import jetbrains.datalore.plot.base.stat.DotplotStat
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider

internal object GeomProviderFactory {
    private val PROVIDER = HashMap<GeomKind, GeomProvider>()

    // Simple provides
    init {
        PROVIDER[GeomKind.LINE] = GeomProvider.line()
        PROVIDER[GeomKind.SMOOTH] = GeomProvider.smooth()
        PROVIDER[GeomKind.BAR] = GeomProvider.bar()
        PROVIDER[GeomKind.HISTOGRAM] = GeomProvider.histogram()
        PROVIDER[GeomKind.TILE] = GeomProvider.tile()
        PROVIDER[GeomKind.BIN_2D] = GeomProvider.bin2d()
        PROVIDER[GeomKind.LINE_RANGE] = GeomProvider.lineRange()
        PROVIDER[GeomKind.CONTOUR] = GeomProvider.contour()
        PROVIDER[GeomKind.CONTOURF] = GeomProvider.contourf()
        PROVIDER[GeomKind.POLYGON] = GeomProvider.polygon()
        PROVIDER[GeomKind.MAP] = GeomProvider.map()
        PROVIDER[GeomKind.AB_LINE] = GeomProvider.abline()
        PROVIDER[GeomKind.H_LINE] = GeomProvider.hline()
        PROVIDER[GeomKind.V_LINE] = GeomProvider.vline()
        PROVIDER[GeomKind.RIBBON] = GeomProvider.ribbon()
        PROVIDER[GeomKind.AREA] = GeomProvider.area()
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

    fun createGeomProvider(geomKind: GeomKind, layerConfig: OptionsAccessor): GeomProvider {
        return when (geomKind) {
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

            GeomKind.ERROR_BAR -> GeomProvider.errorBar { ctx ->
                // Horizontal or vertical
                val isVertical = setOf(Aes.YMIN, Aes.YMAX).any { aes -> ctx.hasBinding(aes) || ctx.hasConstant(aes) }
                val isHorizontal = setOf(Aes.XMIN, Aes.XMAX).any { aes -> ctx.hasBinding(aes) || ctx.hasConstant(aes) }
                require(!(isVertical && isHorizontal)) {
                    "Either ymin, ymax or xmin, xmax must be specified for the errorbar."
                }

                ErrorBarGeom(isVertical)
            }

            GeomKind.CROSS_BAR -> GeomProvider.crossBar {
                val geom = CrossBarGeom()
                if (layerConfig.hasOwn(Option.Geom.CrossBar.FATTEN)) {
                    geom.fattenMidline = layerConfig.getDouble(Option.Geom.CrossBar.FATTEN)!!
                }
                geom
            }

            GeomKind.POINT_RANGE -> GeomProvider.pointRange {
                val geom = PointRangeGeom()
                if (layerConfig.hasOwn(Option.Geom.PointRange.FATTEN)) {
                    geom.fattenMidPoint = layerConfig.getDouble(Option.Geom.PointRange.FATTEN)!!
                }
                geom
            }

            GeomKind.BOX_PLOT -> GeomProvider.boxplot {
                val geom = BoxplotGeom()
                if (layerConfig.hasOwn(Option.Geom.Boxplot.FATTEN)) {
                    geom.fattenMidline = layerConfig.getDouble(Option.Geom.Boxplot.FATTEN)!!
                }
                if (layerConfig.hasOwn(Option.Geom.Boxplot.WHISKER_WIDTH)) {
                    geom.whiskerWidth = layerConfig.getDouble(Option.Geom.Boxplot.WHISKER_WIDTH)!!
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
                geom
            }

            GeomKind.SEGMENT -> GeomProvider.segment {
                val geom = SegmentGeom()
                if (layerConfig.has(Option.Geom.Segment.ARROW)) {
                    val cfg1 = ArrowSpecConfig.create(layerConfig[Option.Geom.Segment.ARROW]!!)
                    geom.arrowSpec = cfg1.createArrowSpec()
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
                applyTextOptions(layerConfig, geom)
                geom
            }

            GeomKind.LABEL -> GeomProvider.label {
                val geom = LabelGeom()

                applyTextOptions(layerConfig, geom)
                layerConfig.getDouble(Option.Geom.Label.LABEL_PADDING)?.let { geom.paddingFactor = it }
                layerConfig.getDouble(Option.Geom.Label.LABEL_R)?.let { geom.radiusFactor = it }
                layerConfig.getDouble(Option.Geom.Label.LABEL_SIZE)?.let { geom.borderWidth = it }

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
                layerConfig.getDouble(Option.Geom.Pie.HOLE)?.let { geom.holeSize = it }
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

            else -> {
                require(PROVIDER.containsKey(geomKind)) { "Provider doesn't support geom kind: '$geomKind'" }
                PROVIDER.getValue(geomKind)
            }
        }
    }

    private fun applyTextOptions(opts: OptionsAccessor, geom: TextGeom) {
        opts.getString(Option.Geom.Text.LABEL_FORMAT)?.let { geom.formatter = StringFormat.forOneArg(it)::format }
        opts.getString(Option.Geom.Text.NA_TEXT)?.let { geom.naValue = it }
        geom.sizeUnit = opts.getString(Option.Geom.Text.SIZE_UNIT)?.lowercase()
    }
}