/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions

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
            const val ERROR_GEN = "error_gen" // for internal use: testing etc.
            const val GG_TOOLBAR = "ggtoolbar"
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
            const val ORDER = "order"

            // Values of the "TYPE" property
            object DateTime {
                const val DATE_TIME = Types.DATE_TIME // TODO: remove. replaced Types.DATE_TIME
                const val TIME_ZONE = "time_zone" // TODO: remove or move to Types
            }

            object Types {
                const val DATE_TIME = "datetime"
                const val INTEGER = "int"
                const val FLOATING = "float"
                const val STRING = "str"
                const val BOOLEAN = "bool"
                const val UNKNOWN = "unknown"
            }
        }
    }

    object ErrorGen {
        const val MESSAGE = "message" // str
        const val IS_INTERNAL = "is_internal" // bool
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

    // Unlisted supported features:
    // - Plot.SIZE (a.k.a. "ggsize")
    // - Plot.THEME
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
            const val SHARE_X_SCALE = "sharex" // all, row, col
            const val SHARE_Y_SCALE = "sharey" // all, row, col

            object Scales {
                const val SHARE_NONE = "none"
                const val SHARE_ALL = "all"
                const val SHARE_ROW = "row"
                const val SHARE_COL = "col"
            }
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
        const val SPEC_OVERRIDE = "spec_override"  // Tools support. Values that tool passes to a FigureModel.
    }

    object Layer {
        const val GEOM = "geom"
        const val STAT = "stat"
        const val POS = "position"
        const val SAMPLING = "sampling"
        const val SHOW_LEGEND = "show_legend"
        const val INHERIT_AES = "inherit_aes"
        const val MANUAL_KEY = "manual_key"
        const val TOOLTIPS = "tooltips"

        const val TOOLTIP_TITLE = "title"
        const val TOOLTIP_ANCHOR = "tooltip_anchor"
        const val TOOLTIP_MIN_WIDTH = "tooltip_min_width"
        const val DISABLE_SPLITTING = "disable_splitting"

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

        object LayerKey {
            const val LABEL = "label"
            const val GROUP = "group"
            const val INDEX = "index"
        }

        const val DEFAULT_LEGEND_GROUP_NAME = "manual"
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

        object Area {
            const val FLAT = "flat"
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
            const val SEED = "seed"
        }

        object Step {
            const val DIRECTION = "direction"
            const val PADDED = "pad"
        }

        object Segment {
            const val ARROW = "arrow"
            const val ANIMATION = "animation"
            const val FLAT = "flat"
            const val GEODESIC = "geodesic"
            const val SPACER = "spacer"
        }

        object Curve {
            const val ARROW = "arrow"
            const val CURVATURE = "curvature"
            const val ANGLE = "angle"
            const val NCP = "ncp"
            const val SPACER = "spacer"
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
            const val CHECK_OVERLAP = "check_overlap"
        }

        object Label {
            const val LABEL_PADDING = "label_padding"
            const val LABEL_R = "label_r"
            const val LABEL_SIZE = "label_size"
            const val ALPHA_STROKE = "alpha_stroke"
        }

        object Pie {
            const val HOLE = "hole"
            const val SPACER_WIDTH = "spacer_width"
            const val SPACER_COLOR = "spacer_color"
            const val STROKE_SIDE = "stroke_side"
            const val SIZE_UNIT = "size_unit"
        }

        object Lollipop {
            const val FATTEN = "fatten"
            const val SLOPE = "slope"
            const val INTERCEPT = "intercept"
            const val DIRECTION = "dir"
        }

        object Spoke {
            const val ARROW = "arrow"
            const val PIVOT = "pivot"
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
            const val THRESHOLD = "threshold"
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

        object ECDF {
            const val N = "n"
            const val PADDED = "pad"
        }

        object Summary {
            const val QUANTILES = "quantiles"
            const val FUN = "fun"
            const val FUN_MIN = "fun_min"
            const val FUN_MAX = "fun_max"

            object Functions {
                const val COUNT = "count"
                const val SUM = "sum"
                const val MEAN = "mean"
                const val MEDIAN = "median"
                const val MIN = "min"
                const val MAX = "max"
                const val LQ = "lq"
                const val MQ = "mq"
                const val UQ = "uq"
            }
        }
    }

    object Pos {
        const val NAME = "name"

        object Dodge {
            const val WIDTH = "width"
        }

        object DodgeV {
            const val HEIGHT = "height"
        }

        object Jitter {
            const val WIDTH = "width"
            const val HEIGHT = "height"
            const val SEED = "seed"
        }

        object Nudge {
            const val WIDTH = "x"
            const val HEIGHT = "y"
        }

        object JitterDodge {
            const val DODGE_WIDTH = "dodge_width"
            const val JITTER_WIDTH = "jitter_width"
            const val JITTER_HEIGHT = "jitter_height"
            const val SEED = "seed"
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
        const val LABLIM = "lablim"
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
        const val X_LABWIDTH = "x_labwidth"
        const val Y_LABWIDTH = "y_labwidth"

        // wrap
        const val FACETS = "facets"
        const val NCOL = "ncol"
        const val NROW = "nrow"
        const val FACETS_ORDER = "order"
        const val FACETS_FILL_DIR = "dir"
        const val FACETS_FORMAT = "format"
        const val FACETS_LABWIDTH = "labwidth"

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

        const val REVERSE = "reverse" // not implemented
        const val TITLE = "title"

        object Legend {
            const val ROW_COUNT = "nrow"
            const val COL_COUNT = "ncol"
            const val BY_ROW = "byrow"
            const val OVERRIDE_AES = "override_aes"
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
        const val POLYGON = "polygon"
    }

    object Theme {
        // System
        const val EXPONENT_FORMAT = ThemeOption.EXPONENT_FORMAT

        // Common
        const val TITLE = ThemeOption.TITLE
        const val TEXT = ThemeOption.TEXT
        const val LINE = ThemeOption.LINE
        const val RECT = ThemeOption.RECT

        const val PLOT_BKGR_RECT = ThemeOption.PLOT_BKGR_RECT
        const val PLOT_TITLE = ThemeOption.PLOT_TITLE
        const val PLOT_SUBTITLE = ThemeOption.PLOT_SUBTITLE
        const val PLOT_CAPTION = ThemeOption.PLOT_CAPTION
        const val PLOT_MESSAGE = ThemeOption.PLOT_MESSAGE
        const val PLOT_MARGIN = ThemeOption.PLOT_MARGIN
        const val PLOT_INSET = ThemeOption.PLOT_INSET
        const val PLOT_TITLE_POSITION = ThemeOption.PLOT_TITLE_POSITION
        const val PLOT_CAPTION_POSITION = ThemeOption.PLOT_CAPTION_POSITION

        // Axis
        const val AXIS = ThemeOption.AXIS

        const val AXIS_ONTOP = ThemeOption.AXIS_ONTOP
        const val AXIS_ONTOP_X = ThemeOption.AXIS_ONTOP_X
        const val AXIS_ONTOP_Y = ThemeOption.AXIS_ONTOP_Y

        const val AXIS_TITLE = ThemeOption.AXIS_TITLE
        const val AXIS_TITLE_X = ThemeOption.AXIS_TITLE_X
        const val AXIS_TITLE_Y = ThemeOption.AXIS_TITLE_Y

        const val AXIS_TEXT = ThemeOption.AXIS_TEXT
        const val AXIS_TEXT_X = ThemeOption.AXIS_TEXT_X
        const val AXIS_TEXT_Y = ThemeOption.AXIS_TEXT_Y

        const val AXIS_TICKS = ThemeOption.AXIS_TICKS
        const val AXIS_TICKS_X = ThemeOption.AXIS_TICKS_X
        const val AXIS_TICKS_Y = ThemeOption.AXIS_TICKS_Y

        const val AXIS_TICKS_LENGTH = ThemeOption.AXIS_TICKS_LENGTH
        const val AXIS_TICKS_LENGTH_X = ThemeOption.AXIS_TICKS_LENGTH_X
        const val AXIS_TICKS_LENGTH_Y = ThemeOption.AXIS_TICKS_LENGTH_Y

        const val AXIS_LINE = ThemeOption.AXIS_LINE
        const val AXIS_LINE_X = ThemeOption.AXIS_LINE_X
        const val AXIS_LINE_Y = ThemeOption.AXIS_LINE_Y

        const val AXIS_TOOLTIP = ThemeOption.AXIS_TOOLTIP
        const val AXIS_TOOLTIP_X = ThemeOption.AXIS_TOOLTIP_X
        const val AXIS_TOOLTIP_Y = ThemeOption.AXIS_TOOLTIP_Y

        const val AXIS_TOOLTIP_TEXT = ThemeOption.AXIS_TOOLTIP_TEXT
        const val AXIS_TOOLTIP_TEXT_X = ThemeOption.AXIS_TOOLTIP_TEXT_X
        const val AXIS_TOOLTIP_TEXT_Y = ThemeOption.AXIS_TOOLTIP_TEXT_Y

        // Panel
        const val PANEL_INSET = ThemeOption.PANEL_INSET
        const val PANEL_BKGR_RECT = ThemeOption.PANEL_BKGR_RECT
        const val PANEL_BORDER_RECT = ThemeOption.PANEL_BORDER_RECT
        const val PANEL_BORDER_ONTOP = ThemeOption.PANEL_BORDER_ONTOP

        // Panel grid
        const val PANEL_GRID = ThemeOption.PANEL_GRID
        const val PANEL_GRID_ONTOP = ThemeOption.PANEL_GRID_ONTOP
        const val PANEL_GRID_ONTOP_X = ThemeOption.PANEL_GRID_ONTOP_X
        const val PANEL_GRID_ONTOP_Y = ThemeOption.PANEL_GRID_ONTOP_Y
        const val PANEL_GRID_MAJOR = ThemeOption.PANEL_GRID_MAJOR
        const val PANEL_GRID_MINOR = ThemeOption.PANEL_GRID_MINOR
        const val PANEL_GRID_MAJOR_X = ThemeOption.PANEL_GRID_MAJOR_X
        const val PANEL_GRID_MINOR_X = ThemeOption.PANEL_GRID_MINOR_X
        const val PANEL_GRID_MAJOR_Y = ThemeOption.PANEL_GRID_MAJOR_Y
        const val PANEL_GRID_MINOR_Y = ThemeOption.PANEL_GRID_MINOR_Y

        // Facet
        const val FACET_STRIP_BGR_RECT = ThemeOption.FACET_STRIP_BGR_RECT
        const val FACET_STRIP_TEXT = ThemeOption.FACET_STRIP_TEXT

        // Legend
        const val LEGEND_BKGR_RECT = ThemeOption.LEGEND_BKGR_RECT
        const val LEGEND_TEXT = ThemeOption.LEGEND_TEXT
        const val LEGEND_TITLE = ThemeOption.LEGEND_TITLE
        const val LEGEND_POSITION = ThemeOption.LEGEND_POSITION
        const val LEGEND_JUSTIFICATION = ThemeOption.LEGEND_JUSTIFICATION
        const val LEGEND_DIRECTION = ThemeOption.LEGEND_DIRECTION

        const val LEGEND_KEY_RECT = ThemeOption.LEGEND_KEY_RECT
        const val LEGEND_KEY_SIZE = ThemeOption.LEGEND_KEY_SIZE
        const val LEGEND_KEY_WIDTH = ThemeOption.LEGEND_KEY_WIDTH
        const val LEGEND_KEY_HEIGHT = ThemeOption.LEGEND_KEY_HEIGHT
        const val LEGEND_KEY_SPACING = ThemeOption.LEGEND_KEY_SPACING
        const val LEGEND_KEY_SPACING_X = ThemeOption.LEGEND_KEY_SPACING_X
        const val LEGEND_KEY_SPACING_Y = ThemeOption.LEGEND_KEY_SPACING_Y

        // Tooltip
        const val TOOLTIP_RECT = ThemeOption.TOOLTIP_RECT
        const val TOOLTIP_TEXT = ThemeOption.TOOLTIP_TEXT
        const val TOOLTIP_TITLE_TEXT = ThemeOption.TOOLTIP_TITLE_TEXT

        // Annotation
        const val ANNOTATION_TEXT = ThemeOption.ANNOTATION_TEXT

        const val GEOM = ThemeOption.GEOM
        const val FLAVOR = ThemeOption.FLAVOR

        // view element
        const val ELEMENT_BLANK = ThemeOption.ELEMENT_BLANK_SHORTHAND

        object Elem {
            const val BLANK = ThemeOption.Elem.BLANK
            const val FILL = ThemeOption.Elem.FILL
            const val COLOR = ThemeOption.Elem.COLOR
            const val SIZE = ThemeOption.Elem.SIZE
            const val LINETYPE = ThemeOption.Elem.LINETYPE
            const val FONT_FAMILY = ThemeOption.Elem.FONT_FAMILY
            const val FONT_FACE = ThemeOption.Elem.FONT_FACE
            const val HJUST = ThemeOption.Elem.HJUST
            const val VJUST = ThemeOption.Elem.VJUST
            const val ANGLE = ThemeOption.Elem.ANGLE
            const val MARGIN = ThemeOption.Elem.MARGIN
        }

        object Name {
            // ggplot2 themes
            const val R_GREY = ThemeOption.Name.R_GREY
            const val R_LIGHT = ThemeOption.Name.R_LIGHT
            const val R_CLASSIC = ThemeOption.Name.R_CLASSIC
            const val R_MINIMAL = ThemeOption.Name.R_MINIMAL
            const val R_BW = ThemeOption.Name.R_BW

            // lets-plot themes
            const val LP_MINIMAL = ThemeOption.Name.LP_MINIMAL
            const val LP_NONE = ThemeOption.Name.LP_NONE
        }

        object Flavor {
            const val DARCULA = ThemeOption.Flavor.DARCULA
            const val SOLARIZED_LIGHT = ThemeOption.Flavor.SOLARIZED_LIGHT
            const val SOLARIZED_DARK = ThemeOption.Flavor.SOLARIZED_DARK
            const val HIGH_CONTRAST_LIGHT = ThemeOption.Flavor.HIGH_CONTRAST_LIGHT
            const val HIGH_CONTRAST_DARK = ThemeOption.Flavor.HIGH_CONTRAST_DARK
        }

        object Geom {
            const val PEN = ThemeOption.Geom.PEN
            const val PAPER = ThemeOption.Geom.PAPER
            const val BRUSH = ThemeOption.Geom.BRUSH
        }
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
        private const val BAND = "band"
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
        private const val CURVE = "curve"
        private const val SPOKE = "spoke"
        const val TEXT = "text"
        const val LABEL = "label"
        private const val RASTER = "raster"
        const val IMAGE = "image"
        const val PIE = "pie"
        const val LOLLIPOP = "lollipop"
        const val BLANK = "blank"

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
            map[BAND] = GeomKind.BAND
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
            map[CURVE] = GeomKind.CURVE
            map[SPOKE] = GeomKind.SPOKE
            map[TEXT] = GeomKind.TEXT
            map[LABEL] = GeomKind.LABEL
            map[RASTER] = GeomKind.RASTER
            map[IMAGE] = GeomKind.IMAGE
            map[PIE] = GeomKind.PIE
            map[LOLLIPOP] = GeomKind.LOLLIPOP
            map[BLANK] = GeomKind.BLANK

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

        fun fromStatKind(statKind: StatKind): String {
            return statKind.name.lowercase()
        }

        fun values(): Set<String> {
            return GEOM_KIND_MAP.keys
        }
    }

    object Coord {
        // coord parameters
        const val X_LIM = "xlim"    // array of two nullable numbers
        const val Y_LIM = "ylim"
        const val RATIO = "ratio"
        const val FLIPPED = "flip"
        const val EXPAND = "expand"  // todo
        const val ORIENTATION = "orientation" // Todo: see 'coord_map'
        const val PROJECTION = "projection"   // todo
        const val THETA = "theta"
        const val START = "start"
        const val DIRECTION = "direction"
        const val TRANSFORM_BKGR = "transform_bkgr"
        const val CLIP = "clip"

        object Projections {
            const val MERCATOR = "mercator"
            const val IDENTITY = "identity"
        }

        object Theta {
            const val X = "x"
            const val Y = "y"
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
        const val LOG2 = "log2"
        const val SYMLOG = "symlog"
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

    object SpecOverride {
        // Tools can temporarily override default or provided limits.
        const val COORD_XLIM_TRANSFORMED = FigureModelOptions.COORD_XLIM_TRANSFORMED  // array of two nullable numbers
        const val COORD_YLIM_TRANSFORMED = FigureModelOptions.COORD_YLIM_TRANSFORMED
    }
}