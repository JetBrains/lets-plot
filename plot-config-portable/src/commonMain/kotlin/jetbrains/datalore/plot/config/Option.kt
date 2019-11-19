/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind

object Option {

    object Meta {
        const val KIND = "kind"
        const val DATA_META = "data_meta"
        const val MAP_DATA_META = "map_data_meta"

        object Kind {
            const val PLOT = "plot"
            const val GG_BUNCH = "ggbunch"
            const val POS = "pos"
        }

        object PubSub {
            const val TAG = "pubsub"
            const val CHANNEL_ID = "channel_id"
            const val COL_NAMES = "col_names"
        }

        object GeoDataFrame {
            const val TAG = "geodataframe"
            const val GEOMETRY = "geometry"
        }

        object GeoReference {
            const val TAG = "georeference"
        }
    }

    object GGBunch {
        const val ITEMS = "items"

        object Item {
            const val X = "x"
            const val Y = "y"
            const val WIDTH = "width"
            const val HEIGHT = "height"
            const val FEATURE_SPEC = "feature_spec"
        }
    }

    object Plot {
        const val DATA = "data"
        // ToDo: merge 'data' options
        const val MAPPING = "mapping"
        const val LAYERS = "layers"
        const val SCALES = "scales"
        const val TITLE = "ggtitle"
        const val TITLE_TEXT = "text"
        const val COORD = "coord"
        const val FACET = "facet"
        const val THEME = "theme"
        const val SIZE = "ggsize"
    }

    object Layer {
        const val GEOM = "geom"
        const val STAT = "stat"
        const val DATA = "data"
        const val MAPPING = "mapping"
        const val POS = "position"
        const val SAMPLING = "sampling"
        const val SHOW_LEGEND = "show_legend"
    }

    object Geom {

        object Choropleth {
            const val GEO_POSITIONS = "map"
        }

        object BoxplotOutlier {
            const val COLOR = "outlier_color"
            const val FILL = "outlier_fill"
            const val SHAPE = "outlier_shape"
            const val SIZE = "outlier_size"
        }

        object Jitter {
            const val WIDTH = "width"
            const val HEIGHT = "height"
        }

        object Step {
            const val DIRECTION = "direction"
        }

        object Segment {
            const val ARROW = "arrow"
            const val ANIMATION = "animation"
        }

        object Path {
            const val ANIMATION = "animation"
        }

        object Point {
            const val ANIMATION = "animation"
        }

        object Image {
            const val HREF = "href"
            const val SPEC = "image_spec"

            object Spec {
                const val WIDTH = "width"
                const val HEIGHT = "height"
                const val TYPE = "type"
                const val BYTES = "bytes"
            }

            object Type {
                const val GRAY = "gray"
                const val RGB = "rgb"
                const val RGBA = "rgba"
            }
        }

        object LiveMap {
            const val PARENT = "within"
            const val DISPLAY_MODE = "display_mode"
            const val FEATURE_LEVEL = "level"
            const val INTERACTIVE = "interactive"
            const val MAGNIFIER = "magnifier"
            const val LOCATION = "location"
            const val ZOOM = "zoom"
            const val STROKE = "stroke"
            const val SCALED = "scaled"
            const val CLUSTERING = "clustering"
            const val LABELS = "labels"
            const val THEME = "theme"
            const val PROJECTION = "projection"
            const val GEODESIC = "geodesic"
            const val DEV_PARAMS = "dev_params"
        }
    }

    object Scale {
        const val NAME = "name"
        const val AES = "aesthetic"
        const val BREAKS = "breaks"
        const val LABELS = "labels"
        const val EXPAND = "expand"
        const val LIMITS = "limits"
        const val DISCRETE_DOMAIN = "discrete"
        const val DATE_TIME = "datetime"
        const val NA_VALUE = "na_value"
        const val GUIDE = "guide"

        // continuous scale
        const val CONTINUOUS_TRANSFORM = "trans"

        // discrete scale output values
        const val OUTPUT_VALUES = "values"
        // shape scale
        const val SHAPE_SOLID = "solid"
        // gradient scale
        const val LOW = "low"
        const val MID = "mid"
        const val HIGH = "high"
        const val MIDPOINT = "midpoint"
        // hue scale
        const val HUE_RANGE = "h"
        const val CHROMA = "c"
        const val LUMINANCE = "l"
        const val START_HUE = "h_start"
        const val DIRECTION = "direction"
        // grey scale
        const val START = "start"
        const val END = "end"
        // color brewer
        const val PALETTE_TYPE = "type"
        const val PALETTE = "palette"

        // range
        const val RANGE = "range"
        // max_size for scale_size_area
        const val MAX_SIZE = "max_size"

        const val SCALE_MAPPER_KIND = "scale_mapper_kind"
    }

    object Facet {
        const val NAME = "name"
        const val X = "x"
        const val Y = "y"
    }

    object Mapping {
        const val GROUP = "group"
        val MAP_ID =
            toOption(Aes.MAP_ID)    // map_id is 'aes' but also used as option in geom_map()
        private val AES_BY_OPTION = HashMap<String, Aes<*>>()
        val REAL_AES_OPTION_NAMES: Iterable<String> = AES_BY_OPTION.keys

        init {
            for (aes in Aes.values()) {
                AES_BY_OPTION[toOption(
                    aes
                )] = aes
            }
            // aliases
            AES_BY_OPTION["colour"] = Aes.COLOR
            AES_BY_OPTION["col"] = Aes.COLOR
        }

        fun toAes(option: String): Aes<*> {
            checkArgument(AES_BY_OPTION.containsKey(option), "Not an aesthetic: '$option'")
            return AES_BY_OPTION[option]!!
        }

        fun toOption(aes: Aes<*>): String {
            return aes.name.toLowerCase()
        }
    }

    object Guide {
        const val NONE = "none"
        const val LEGEND = "legend"
        const val COLOR_BAR = "colorbar"
        const val COLOR_BAR_GB = "colourbar"

        const val REVERSE = "reverse"

        object Legend {
            const val ROW_COUNT = "nrow"
            const val COL_COUNT = "ncol"
            const val BY_ROW = "byrow"
        }

        object ColorBar {
            const val WIDTH = "barwidth"
            const val HEIGHT = "barheight"
            const val BIN_COUNT = "nbin"
        }
    }

    object Arrow {
        const val ANGLE = "angle"
        const val LENGTH = "length"
        const val ENDS = "ends"
        const val TYPE = "type"
    }

    internal object Sampling {
        const val NONE = "none"
        const val N = "n"
        const val SEED = "seed"
        const val MIN_SUB_SAMPLE = "min_subsample"
    }

    object Theme {
        const val AXIS_LINE = "axis_line"
        const val AXIS_TICKS = "axis_ticks"
        const val AXIS_TEXT = "axis_text"
        // tick labels
        const val AXIS_TITLE = "axis_title"
        const val LEGEND_POSITION = "legend_position"
        const val LEGEND_JUSTIFICATION = "legend_justification"
        const val LEGEND_DIRECTION = "legend_direction"
    }

    object GeomName {
        private const val PATH = "path"
        private const val LINE = "line"
        private const val SMOOTH = "smooth"
        private const val BAR = "bar"
        const val HISTOGRAM = "histogram"
        private const val TILE = "tile"
        private const val MAP = "map"
        private const val ERROR_BAR = "errorbar"
        const val POLYGON = "polygon"
        private const val AB_LINE = "abline"
        private const val H_LINE = "hline"
        private const val V_LINE = "vline"
        private const val BOX_PLOT = "boxplot"
        const val LIVE_MAP = "livemap"
        const val POINT = "point"
        private const val RIBBON = "ribbon"
        private const val AREA = "area"
        private const val DENSITY = "density"
        private const val CONTOUR = "contour"
        private const val CONTOURF = "contourf"
        private const val DENSITY2D = "density2d"
        private const val DENSITY2DF = "density2df"
        private const val JITTER = "jitter"
        private const val FREQPOLY = "freqpoly"
        private const val STEP = "step"
        private const val RECT = "rect"
        private const val SEGMENT = "segment"
        private const val TEXT = "text"
        private const val RASTER = "raster"
        const val IMAGE = "image"

        private val GEOM_KIND_MAP: Map<String, GeomKind>

        init {
            val map = HashMap<String, GeomKind>()
            map[PATH] = GeomKind.PATH
            map[LINE] = GeomKind.LINE
            map[SMOOTH] = GeomKind.SMOOTH
            map[BAR] = GeomKind.BAR
            map[HISTOGRAM] = GeomKind.HISTOGRAM
            map[TILE] = GeomKind.TILE
            map[MAP] = GeomKind.MAP
            map[ERROR_BAR] = GeomKind.ERROR_BAR
            map[POLYGON] = GeomKind.POLYGON
            map[AB_LINE] = GeomKind.AB_LINE
            map[H_LINE] = GeomKind.H_LINE
            map[V_LINE] = GeomKind.V_LINE
            map[BOX_PLOT] = GeomKind.BOX_PLOT
            map[LIVE_MAP] = GeomKind.LIVE_MAP
            map[POINT] = GeomKind.POINT
            map[RIBBON] = GeomKind.RIBBON
            map[AREA] = GeomKind.AREA
            map[DENSITY] = GeomKind.DENSITY
            map[CONTOUR] = GeomKind.CONTOUR
            map[CONTOURF] = GeomKind.CONTOURF
            map[DENSITY2D] = GeomKind.DENSITY2D
            map[DENSITY2DF] = GeomKind.DENSITY2DF
            map[JITTER] = GeomKind.JITTER
            map[FREQPOLY] = GeomKind.FREQPOLY
            map[STEP] = GeomKind.STEP
            map[RECT] = GeomKind.RECT
            map[SEGMENT] = GeomKind.SEGMENT
            map[TEXT] = GeomKind.TEXT
            map[RASTER] = GeomKind.RASTER
            map[IMAGE] = GeomKind.IMAGE
            GEOM_KIND_MAP = map
        }

        fun toGeomKind(geomName: String): GeomKind {
            if (!GEOM_KIND_MAP.containsKey(geomName)) {
                throw IllegalArgumentException("Unknown geom name: '$geomName'")
            }

            return GEOM_KIND_MAP[geomName]!!
        }

        fun values(): Set<String> {
            return GEOM_KIND_MAP.keys
        }
    }

    object CoordName {
        // coordinate systems
        const val CARTESIAN = "cartesian"
        const val FIXED = "fixed"
        const val MAP = "map"
        const val QUICK_MAP = "quickmap"  // todo
        const val FLIP = "flip"      // todo
        const val EQUAL = "equal"    // todo
        const val POLAR = "polar"    // todo
        const val TRANS = "trans"    // todo
    }
}