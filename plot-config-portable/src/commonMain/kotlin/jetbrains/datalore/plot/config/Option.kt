/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK_SHORTHAND

object Option {

    object Meta {
        const val KIND = "kind"
        const val NAME = "name"
        const val DATA_META = "data_meta"
        const val MAP_DATA_META = "map_data_meta"

        object Kind {
            const val PLOT = "plot"
            const val SUBPLOTS = "subplots"
            const val GG_BUNCH = "ggbunch"
        }

        object PubSub {
            const val TAG = "pubsub"
            const val CHANNEL_ID = "channel_id"
            const val COL_NAMES = "col_names"
        }

        object GeoDataFrame {
            const val GDF = "geodataframe"
            const val GEOMETRY = "geometry"
        }

        object GeoReference {
            const val GEOREFERENCE = "georeference"
            const val REQUEST = "request"
            const val MAP_REGION_COLUMN = "region"

            object Columns {
                const val ID = "id"
                const val POSITION = "position"
                const val LIMIT = "limit"
                const val CENTROID = "centroid"
            }
        }

        object MappingAnnotation {
            const val TAG = "mapping_annotations"
            const val AES = "aes"
            const val ANNOTATION = "annotation"
            const val AS_DISCRETE = "as_discrete"
            const val PARAMETERS = "parameters"
            const val LABEL = "label"
            const val ORDER_BY = "order_by"
            const val ORDER = "order"
        }

        object SeriesAnnotation {
            const val TAG = "series_annotations"

            const val COLUMN = "column"   // a.k.a. variable name
            const val TYPE = "type"
            const val FACTOR_LEVELS = "factor_levels" // annotation for discrete variables

            // Values of the "TYPE" property
            object DateTime {
                const val DATE_TIME = "datetime"
                const val TIME_ZONE = "time_zone"
            }
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

    object SubPlots {
        const val FIGURES = "figures"
        const val LAYOUT = "layout"

        object Figure {
            const val BLANK = "blank"
        }

        object Layout {
            const val NAME = Meta.NAME
            const val SUBPLOTS_GRID = "grid"
        }

        object Grid {
            const val NCOLS = "ncol"
            const val NROWS = "nrow"
            const val HSPACE = "hspace"  // px
            const val VSPACE = "vspace"  // px
            const val COL_WIDTHS = "widths"
            const val ROW_HEIGHTS = "heights"
            const val FIT_CELL_ASPECT_RATIO = "fit" // bool
            const val INNER_ALIGNMENT = "align" // bool
        }
    }

    object PlotBase {
        const val DATA = "data"
        const val MAPPING = "mapping"
    }

    object Plot {
        const val BISTRO = "bistro"
        const val LAYERS = "layers"
        const val SCALES = "scales"
        const val TITLE = "ggtitle"
        const val TITLE_TEXT = "text"
        const val SUBTITLE_TEXT = "subtitle"
        const val CAPTION = "caption"
        const val CAPTION_TEXT = "text"
        const val COORD = "coord"
        const val FACET = "facet"
        const val THEME = "theme"
        const val SIZE = "ggsize"
        const val GUIDES = "guides"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val METAINFO_LIST = "metainfo_list"
        const val METAINFO = "metainfo"  // a special kind of FeatureSpec
    }

    object Layer {
        const val GEOM = "geom"
        const val STAT = "stat"
        const val POS = "position"
        const val SAMPLING = "sampling"
        const val SHOW_LEGEND = "show_legend"
        const val TOOLTIPS = "tooltips"
        const val TOOLTIP_ANCHOR = "tooltip_anchor"
        const val TOOLTIP_MIN_WIDTH = "tooltip_min_width"
        const val NONE = "none"
        const val MAP_JOIN = "map_join"
        const val USE_CRS = "use_crs"
        const val ORIENTATION = "orientation"
        const val MARGINAL = "marginal"

        const val ANNOTATIONS = "labels"
        const val ANNOTATION_SIZE = "annotation_size"

        const val COLOR_BY = "color_by"
        const val FILL_BY = "fill_by"

        object Marginal {
            const val SIZE = "margin_size"
            const val SIZE_DEFAULT = 0.1
            const val SIDE = "margin_side"
            const val SIDE_LEFT = "l"
            const val SIDE_RIGHT = "r"
            const val SIDE_TOP = "t"
            const val SIDE_BOTTOM = "b"
        }

        object CRS {
            const val PROVIDED = "provided"
        }
    }

    object LinesSpec {
        const val LINES = "lines"
        const val FORMATS = "formats"
        const val VARIABLES = "variables"
        const val TITLE = "title"

        object Format {
            const val FIELD = "field"
            const val FORMAT = "format"
        }
    }

    object Geom {

        object Density {
            const val QUANTILE_LINES = "quantile_lines"
        }

        object Dotplot {
            const val DOTSIZE = "dotsize"
            const val STACKRATIO = "stackratio"
            const val STACKGROUPS = "stackgroups"
            const val STACKDIR = "stackdir"
            const val METHOD = "method"
        }

        object Choropleth {
            const val GEO_POSITIONS = "map"
        }

        object CrossBar {
            const val FATTEN = "fatten"
        }

        object PointRange {
            const val FATTEN = "fatten"
        }

        object Boxplot {
            const val FATTEN = "fatten"
            const val WHISKER_WIDTH = "whisker_width"
        }

        object BoxplotOutlier {
            const val COLOR = "outlier_color"
            const val FILL = "outlier_fill"
            const val SHAPE = "outlier_shape"
            const val SIZE = "outlier_size"
            const val STROKE = "outlier_stroke"
        }

        object AreaRidges {
            const val SCALE = "scale"
            const val MIN_HEIGHT = "min_height"
            const val QUANTILE_LINES = "quantile_lines"
        }

        object Violin {
            const val QUANTILE_LINES = "quantile_lines"
            const val SHOW_HALF = "show_half"
        }

        object YDotplot {
            const val DOTSIZE = "dotsize"
            const val STACKRATIO = "stackratio"
            const val STACKGROUPS = "stackgroups"
            const val STACKDIR = "stackdir"
            const val METHOD = "method"
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
            const val FLAT = "flat"
            const val GEODESIC = "geodesic"
        }

        object Path {
            const val ANIMATION = "animation"
            const val FLAT = "flat"
            const val GEODESIC = "geodesic"
        }

        object Point {
            const val ANIMATION = "animation"
            const val SIZE_UNIT = "size_unit"
        }

        object Image {
            const val HREF = "href"

            // Parameters in GeomImage.
            // It's "rendered" aesthetics at the same time but "geom image" doesn't use aes to actually render image.
            // The constants are used to compute limits on x/y-axis and the image bbox.
            val XMIN = Aes.XMIN.name
            val XMAX = Aes.XMAX.name
            val YMIN = Aes.YMIN.name
            val YMAX = Aes.YMAX.name
        }

        object Text {
            const val LABEL_FORMAT = "label_format"
            const val NA_TEXT = "na_text"
            const val SIZE_UNIT = "size_unit"
            const val NUDGE_X = "nudge_x"
            const val NUDGE_Y = "nudge_y"
        }

        object Label {
            const val LABEL_PADDING = "label_padding"
            const val LABEL_R = "label_r"
            const val LABEL_SIZE = "label_size"
        }

        object Pie {
            const val HOLE = "hole"
            const val STROKE = "stroke"
            const val STROKE_COLOR = "stroke_color"
        }

        object LiveMap {
            const val INTERACTIVE = "interactive"
            const val LOCATION = "location"
            const val ZOOM = "zoom"
            const val STROKE = "stroke"
            const val CLUSTERING = "clustering"
            const val LABELS = "labels"
            const val THEME = "theme"
            const val PROJECTION = "projection"
            const val SHOW_COORD_PICK_TOOLS = "show_coord_pick_tools"
            const val DATA_SIZE_ZOOMIN = "data_size_zoomin"
            const val CONST_SIZE_ZOOMIN = "const_size_zoomin"
            const val TILES = "tiles"
            const val GEOCODING = "geocoding"
            const val DEV_PARAMS = "dev_params"

            object Tile {
                const val KIND = "kind"
                const val URL = "url"
                const val THEME = "theme"
                const val ATTRIBUTION = "attribution"
                const val MIN_ZOOM = "min_zoom"
                const val MAX_ZOOM = "max_zoom"
                const val FILL_COLOR = "fill_color"

                const val KIND_VECTOR_LETS_PLOT = "vector_lets_plot"
                const val KIND_RASTER_ZXY = "raster_zxy"
                const val KIND_SOLID = "solid"
                const val KIND_CHESSBOARD = "chessboard"

                const val THEME_COLOR = "color"
                const val THEME_LIGHT = "light"
                const val THEME_DARK = "dark"
            }
        }

        object SizeUnit {
            const val X = "x"
            const val Y = "y"
        }
    }

    object Stat {
        object Boxplot {
            const val COEF = "coef"
            const val VARWIDTH = "varwidth"
        }

        object Bin {
            const val BINS = "bins"
            const val BINWIDTH = "binwidth"
            const val METHOD = "method"
            const val CENTER = "center"
            const val BOUNDARY = "boundary"
        }

        object Bin2d {
            const val BINS = "bins"
            const val BINWIDTH = "binwidth"
            const val DROP = "drop"
        }

        object Contour {
            const val BINS = "bins"
            const val BINWIDTH = "binwidth"
        }

        object Corr {
            const val METHOD = "method"
            const val TYPE = "type"
            const val FILL_DIAGONAL = "diag"
            const val THRESHOLD = "threshold"
        }

        object Smooth {
            const val POINT_COUNT = "n"
            const val METHOD = "method"
            const val CONFIDENCE_LEVEL = "level"
            const val DISPLAY_CONFIDENCE_INTERVAL = "se"
            const val SPAN = "span"
            const val POLYNOMIAL_DEGREE = "deg"
            const val LOESS_CRITICAL_SIZE = "max_n"
            const val SAMPLING_SEED = "seed"
        }

        object Density {
            const val N = "n"
            const val KERNEL = "kernel"
            const val BAND_WIDTH = "bw"     // number or string (method name)
            const val ADJUST = "adjust"
            const val FULL_SCAN_MAX = "fs_max"  // use 'full scan' when the input size is < 'fs_max'
            const val TRIM = "trim"
            const val QUANTILES = "quantiles"
        }

        object Density2d {
            const val N = "n"
            const val KERNEL = "kernel"
            const val BAND_WIDTH = "bw"     // list of two numbers, one number or string (method name)
            const val ADJUST = "adjust"
            const val IS_CONTOUR = "contour"
            const val BINS = "bins"
            const val BINWIDTH = "binwidth"
        }

        object DensityRidges {
            const val TRIM = "trim"
            const val TAILS_CUTOFF = "tails_cutoff"
            const val QUANTILES = "quantiles"
        }

        object YDensity {
            const val SCALE = "scale"
            const val TRIM = "trim"
            const val TAILS_CUTOFF = "tails_cutoff"
            const val QUANTILES = "quantiles"
        }

        object QQ {
            const val DISTRIBUTION = "distribution"
            const val DISTRIBUTION_PARAMETERS = "dparams"
        }

        object QQLine {
            const val DISTRIBUTION = "distribution"
            const val DISTRIBUTION_PARAMETERS = "dparams"
            const val LINE_QUANTILES = "quantiles"
        }
    }

    object Pos {
        object Dodge {
            const val WIDTH = "width"
        }
        object DodgeV {
            const val HEIGHT = "height"
        }

        object Jitter {
            const val WIDTH = "width"
            const val HEIGHT = "height"
        }

        object Nudge {
            const val WIDTH = "x"
            const val HEIGHT = "y"
        }

        object JitterDodge {
            const val DODGE_WIDTH = "dodge_width"
            const val JITTER_WIDTH = "jitter_width"
            const val JITTER_HEIGHT = "jitter_height"
        }

        object Stack {
            const val VJUST = "vjust"
            const val MODE = "mode"
        }

        object Fill {
            const val VJUST = "vjust"
            const val MODE = "mode"
        }
    }

    object Scale {
        const val NAME = Meta.NAME
        const val AES = "aesthetic"
        const val BREAKS = "breaks"
        const val LABELS = "labels"
        const val EXPAND = "expand"
        const val LIMITS = "limits"
        const val DISCRETE_DOMAIN = "discrete"
        const val DISCRETE_DOMAIN_REVERSE = "reverse"
        const val DATE_TIME = "datetime"
        const val TIME = "time"
        const val NA_VALUE = "na_value"
        const val GUIDE = "guide"
        const val FORMAT = "format"

        // position of X/Y-axis.
        const val POSITION = "position"
        const val POSITION_L = "left"
        const val POSITION_R = "right"
        const val POSITION_T = "top"
        const val POSITION_B = "bottom"
        const val POSITION_BOTH = "both"

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
        const val COLORS = "colors"

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

        object MapperKind {
            const val IDENTITY = "identity"
            const val COLOR_GRADIENT = "color_gradient"
            const val COLOR_GRADIENT2 = "color_gradient2"
            const val COLOR_GRADIENTN = "color_gradientn"
            const val COLOR_HUE = "color_hue"
            const val COLOR_GREY = "color_grey"
            const val COLOR_BREWER = "color_brewer"
            const val COLOR_CMAP = "color_cmap"
            const val SIZE_AREA = "size_area"
        }

        object Viridis {
            const val CMAP_NAME = "option"
            const val ALPHA = "alpha"
            const val BEGIN = "begin"
            const val END = "end"
            const val DIRECTION = "direction"
        }
    }

    object Facet {
        const val NAME = Meta.NAME
        const val NAME_GRID = "grid"
        const val NAME_WRAP = "wrap"

        const val SCALES = "scales"
        const val SCALES_FIXED = "fixed"
        const val SCALES_FREE = "free"
        const val SCALES_FREE_X = "free_x"
        const val SCALES_FREE_Y = "free_y"

        // grid
        const val X = "x"
        const val Y = "y"
        const val X_ORDER = "x_order"
        const val Y_ORDER = "y_order"
        const val X_FORMAT = "x_format"
        const val Y_FORMAT = "y_format"

        // wrap
        const val FACETS = "facets"
        const val NCOL = "ncol"
        const val NROW = "nrow"
        const val FACETS_ORDER = "order"
        const val FACETS_FILL_DIR = "dir"
        const val FACETS_FORMAT = "format"

        const val FACET_ORDER_ASC = 1
        const val FACET_ORDER_DESC = -1
        const val FACET_FILL_VERT = "v"
        const val FACET_FILL_HOR = "h"
    }

    object Mapping {
        const val GROUP = "group"
        private val AES_BY_OPTION = HashMap<String, Aes<*>>()
        val REAL_AES_OPTION_NAMES: Iterable<String> = AES_BY_OPTION.keys

        init {
            Aes.values().forEach { aes ->
                AES_BY_OPTION[toOption(aes)] = aes
            }
            // aliases
            AES_BY_OPTION["colour"] = Aes.COLOR
            AES_BY_OPTION["col"] = Aes.COLOR
        }

        fun toAes(option: String): Aes<*> {
            require(AES_BY_OPTION.containsKey(option)) { "Not an aesthetic: '$option'" }
            return AES_BY_OPTION[option]!!
        }

        fun toOption(aes: Aes<*>): String {
            return aes.name.lowercase()
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
        const val FLAVOR = "flavor"

        // All other options were moved to
        // jetbrains.datalore.plot.builder.theme2.values.ThemeOption

        // view element
        const val ELEMENT_BLANK = ELEMENT_BLANK_SHORTHAND
    }

    object GeomName {
        private const val PATH = "path"
        private const val LINE = "line"
        private const val SMOOTH = "smooth"
        private const val BAR = "bar"
        const val HISTOGRAM = "histogram"
        private const val DOT_PLOT = "dotplot"
        private const val TILE = "tile"
        private const val BIN_2D = "bin2d"
        private const val MAP = "map"
        private const val ERROR_BAR = "errorbar"
        private const val CROSS_BAR = "crossbar"
        private const val LINE_RANGE = "linerange"
        private const val POINT_RANGE = "pointrange"
        const val POLYGON = "polygon"
        private const val AB_LINE = "abline"
        private const val H_LINE = "hline"
        private const val V_LINE = "vline"
        const val BOX_PLOT = "boxplot"
        private const val AREA_RIDGES = "area_ridges"
        private const val VIOLIN = "violin"
        const val Y_DOT_PLOT = "ydotplot"
        const val LIVE_MAP = "livemap"
        const val POINT = "point"
        private const val RIBBON = "ribbon"
        private const val AREA = "area"
        private const val DENSITY = "density"
        private const val CONTOUR = "contour"
        private const val CONTOURF = "contourf"
        private const val DENSITY2D = "density2d"
        private const val DENSITY2DF = "density2df"
        const val JITTER = "jitter"
        private const val Q_Q = "qq"
        private const val Q_Q_2 = "qq2"
        private const val Q_Q_LINE = "qq_line"
        private const val Q_Q_2_LINE = "qq2_line"
        private const val FREQPOLY = "freqpoly"
        private const val STEP = "step"
        private const val RECT = "rect"
        private const val SEGMENT = "segment"
        const val TEXT = "text"
        const val LABEL = "label"
        private const val RASTER = "raster"
        const val IMAGE = "image"
        const val PIE = "pie"

        private val GEOM_KIND_MAP: Map<String, GeomKind>

        init {
            val map = HashMap<String, GeomKind>()
            map[PATH] = GeomKind.PATH
            map[LINE] = GeomKind.LINE
            map[SMOOTH] = GeomKind.SMOOTH
            map[BAR] = GeomKind.BAR
            map[HISTOGRAM] = GeomKind.HISTOGRAM
            map[DOT_PLOT] = GeomKind.DOT_PLOT
            map[Y_DOT_PLOT] = GeomKind.Y_DOT_PLOT
            map[TILE] = GeomKind.TILE
            map[BIN_2D] = GeomKind.BIN_2D
            map[MAP] = GeomKind.MAP
            map[ERROR_BAR] = GeomKind.ERROR_BAR
            map[CROSS_BAR] = GeomKind.CROSS_BAR
            map[LINE_RANGE] = GeomKind.LINE_RANGE
            map[POINT_RANGE] = GeomKind.POINT_RANGE
            map[POLYGON] = GeomKind.POLYGON
            map[AB_LINE] = GeomKind.AB_LINE
            map[H_LINE] = GeomKind.H_LINE
            map[V_LINE] = GeomKind.V_LINE
            map[BOX_PLOT] = GeomKind.BOX_PLOT
            map[AREA_RIDGES] = GeomKind.AREA_RIDGES
            map[VIOLIN] = GeomKind.VIOLIN
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
            map[Q_Q] = GeomKind.Q_Q
            map[Q_Q_2] = GeomKind.Q_Q_2
            map[Q_Q_LINE] = GeomKind.Q_Q_LINE
            map[Q_Q_2_LINE] = GeomKind.Q_Q_2_LINE
            map[FREQPOLY] = GeomKind.FREQPOLY
            map[STEP] = GeomKind.STEP
            map[RECT] = GeomKind.RECT
            map[SEGMENT] = GeomKind.SEGMENT
            map[TEXT] = GeomKind.TEXT
            map[LABEL] = GeomKind.LABEL
            map[RASTER] = GeomKind.RASTER
            map[IMAGE] = GeomKind.IMAGE
            map[PIE] = GeomKind.PIE
            GEOM_KIND_MAP = map
        }

        fun toGeomKind(geomName: String): GeomKind {
            if (!GEOM_KIND_MAP.containsKey(geomName)) {
                throw IllegalArgumentException("Unknown geom name: '$geomName'")
            }

            return GEOM_KIND_MAP[geomName]!!
        }

        fun fromGeomKind(geomKind: GeomKind): String {
            return GEOM_KIND_MAP.entries.firstOrNull { (_, kind) -> kind == geomKind }?.key
                ?: throw IllegalArgumentException("Unknown geom: '${geomKind.name.lowercase()}'")
        }

        fun values(): Set<String> {
            return GEOM_KIND_MAP.keys
        }
    }

    object Coord {
        // coord parameters
        const val X_LIM = "xlim"
        const val Y_LIM = "ylim"
        const val RATIO = "ratio"
        const val FLIPPED = "flip"
        const val EXPAND = "expand"  // todo
        const val ORIENTATION = "orientation" // Todo: see 'coord_map'
        const val PROJECTION = "projection"   // todo

        object Projections {
            const val MERCATOR = "mercator"
            const val IDENTITY = "identity"
        }
    }

    object CoordName {
        // coordinate systems
        const val CARTESIAN = "cartesian"
        const val FIXED = "fixed"
        const val MAP = "map"
        const val FLIP = "flip"
        const val QUICK_MAP = "quickmap"  // todo
        const val EQUAL = "equal"    // todo
        const val POLAR = "polar"    // todo
        const val TRANS = "trans"    // todo
    }

    object TransformName {
        const val IDENTITY = "identity"
        const val LOG10 = "log10"
        const val REVERSE = "reverse"
        const val SQRT = "sqrt"
    }

    object PlotMetainfo {
        const val FONT_METRICS_ADJUSTMENT = "font_metrics_adjustment"
        const val FONT_FAMILY_INFO = "font_family_info"
    }

    object FontMetainfo {
        const val FAMILY = "family"
        const val WIDTH_CORRECTION = "width_correction"
        const val MONOSPACED = "monospaced"
    }
}