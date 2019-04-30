package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.base.observable.collections.Collections

object Option {

    object Meta {
        val KIND = "kind"
        val DATA_META = "data_meta"
        val MAP_DATA_META = "map_data_meta"

        object Kind {
            val PLOT = "plot"
            val GG_BUNCH = "ggbunch"
        }

        object PubSub {
            val TAG = "pubsub"
            val CHANNEL_ID = "channel_id"
            val COL_NAMES = "col_names"
        }

        object GeoDataFrame {
            val TAG = "geodataframe"
            val GEOMETRY = "base/geometry"
        }

        object GeoReference {
            val TAG = "georeference"
        }
    }

    object GGBunch {
        val ITEMS = "items"

        object Item {
            val X = "x"
            val Y = "y"
            val WIDTH = "width"
            val HEIGHT = "height"
            val FEATURE_SPEC = "feature_spec"
        }
    }

    object Plot {
        val DATA = "data"
        // ToDo: merge 'data' options
        val MAPPING = "mapping"
        val LAYERS = "layers"
        val SCALES = "scales"
        val TITLE = "ggtitle"
        val TITLE_TEXT = "text"
        val COORD = "coord"
        val FACET = "facet"
        val THEME = "theme"
        val SIZE = "ggsize"
    }

    object Layer {
        val GEOM = "geom"
        val STAT = "stat"
        val DATA = "data"
        val MAPPING = "mapping"
        val POS = "position"
        val SAMPLING = "sampling"
        val SHOW_LEGEND = "show_legend"
    }

    object Geom {

        object Choropleth {
            val GEO_POSITIONS = "map"
        }

        object BoxplotOutlier {
            val COLOR = "outlier_color"
            val FILL = "outlier_fill"
            val SHAPE = "outlier_shape"
            val SIZE = "outlier_size"
        }

        object Jitter {
            val WIDTH = "width"
            val HEIGHT = "height"
        }

        object Step {
            val DIRECTION = "direction"
        }

        object Segment {
            val ARROW = "arrow"
            val ANIMATION = "animation"
        }

        object Path {
            val ANIMATION = "animation"
        }

        object Point {
            val ANIMATION = "animation"
        }

        object Image {
            val HREF = "href"
            val SPEC = "image_spec"

            object Spec {
                val WIDTH = "width"
                val HEIGHT = "height"
                val TYPE = "type"
                val BYTES = "bytes"
            }

            object Type {
                val GRAY = "gray"
                val RGB = "rgb"
                val RGBA = "rgba"
            }
        }

        object Livemap {
            val PARENT = "within"
            val DISPLAY_MODE = "display_mode"
            val FEATURE_LEVEL = "level"
            val INTERACTIVE = "interactive"
            val MAGNIFIER = "magnifier"
            val LOCATION = "location"
            val ZOOM = "zoom"
            val STROKE = "stroke"
            val SCALED = "scaled"
            val CLUSTERING = "clustering"
            val LABELS = "labels"
            val THEME = "theme"
            val PROJECTION = "projection"
            val GEODESIC = "geodesic"
            val DEV_PARAMS = "dev_params"
        }
    }

    object Scale {
        val NAME = "name"
        val AES = "aesthetic"
        val BREAKS = "breaks"
        val LABELS = "labels"
        val EXPAND = "expand"
        val LIMITS = "limits"
        val DISCRETE_DOMAIN = "discrete"
        val DATE_TIME = "base/datetime"
        val NA_VALUE = "na_value"
        val GUIDE = "guide"

        // continuous scale
        val CONTINUOUS_TRANSFORM = "trans"

        // discrete scale output values
        val OUTPUT_VALUES = "base/values"
        // shape scale
        val SHAPE_SOLID = "solid"
        // gradient scale
        val LOW = "low"
        val MID = "mid"
        val HIGH = "high"
        val MIDPOINT = "midpoint"
        // hue scale
        val HUE_RANGE = "h"
        val CHROMA = "c"
        val LUMINANCE = "l"
        val START_HUE = "h_start"
        val DIRECTION = "direction"
        // grey scale
        val START = "start"
        val END = "end"
        // color brewer
        val PALETTE_TYPE = "type"
        val PALETTE = "palette"

        // range
        val RANGE = "range"
        // max_size for scale_size_area
        val MAX_SIZE = "max_size"

        val SCALE_MAPPER_KIND = "scale_mapper_kind"
    }

    object Facet {
        val NAME = "name"
        val X = "x"
        val Y = "y"
    }

    object Mapping {
        val GROUP = "group"
        val MAP_ID = toOption(Aes.MAP_ID)    // map_id is 'aes' but also used as option in geom_map()
        private val AES_BY_OPTION = HashMap<String, Aes<*>>()
        val REAL_AES_OPTION_NAMES: Iterable<String> = Collections.unmodifiableCollection(AES_BY_OPTION.keys)

        init {
            for (aes in Aes.values()) {
                AES_BY_OPTION[toOption(aes)] = aes
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
            return aes.name().toLowerCase()
        }
    }

    object Guide {
        val NONE = "none"
        val LEGEND = "legend"
        val COLOR_BAR = "colorbar"
        val COLOR_BAR_GB = "colourbar"

        val REVERSE = "reverse"

        object Legend {
            val ROW_COUNT = "nrow"
            val COL_COUNT = "ncol"
            val BY_ROW = "byrow"
        }

        object ColorBar {
            val WIDTH = "barwidth"
            val HEIGHT = "barheight"
            val BIN_COUNT = "nbin"
        }
    }

    object Arrow {
        val ANGLE = "angle"
        val LENGTH = "length"
        val ENDS = "ends"
        val TYPE = "type"
    }

    internal object Sampling {
        val NONE = "none"
        val N = "n"
        val SEED = "seed"
        val MIN_SUB_SAMPLE = "min_subsample"
    }

    object Theme {
        val AXIS_LINE = "axis_line"
        val AXIS_TICKS = "axis_ticks"
        val AXIS_TEXT = "axis_text"
        // tick labels
        val AXIS_TITLE = "axis_title"
        val LEGEND_POSITION = "legend_position"
        val LEGEND_JUSTIFICATION = "legend_justification"
        val LEGEND_DIRECTION = "legend_direction"
    }

    object GeomName {
        val PATH = "path"
        val LINE = "line"
        val SMOOTH = "smooth"
        val BAR = "bar"
        val HISTOGRAM = "histogram"
        val TILE = "tile"
        val MAP = "map"
        val ERROR_BAR = "errorbar"
        val POLYGON = "polygon"
        val AB_LINE = "abline"
        val H_LINE = "hline"
        val V_LINE = "vline"
        val BOX_PLOT = "boxplot"
        val LIVE_MAP = "livemap"
        val POINT = "point"
        val RIBBON = "ribbon"
        val AREA = "area"
        val DENSITY = "density"
        val CONTOUR = "contour"
        val CONTOURF = "contourf"
        val DENSITY2D = "density2d"
        val DENSITY2DF = "density2df"
        val JITTER = "jitter"
        val FREQPOLY = "freqpoly"
        val STEP = "step"
        val RECT = "rect"
        val SEGMENT = "segment"
        val TEXT = "text"
        val RASTER = "raster"
        val IMAGE = "image"

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
            GEOM_KIND_MAP = Collections.unmodifiableMap(map)
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
}