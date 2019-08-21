package jetbrains.livemap.tiles

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.gis.tileprotocol.mapConfig.Style
import kotlin.math.round

typealias Runnable = () -> Unit

interface Symbolizer {

    fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<Runnable>
    fun applyTo(ctx: Context2d)

    class PolygonSymbolizer internal constructor(private val myStyle: Style) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<Runnable> {
            val tasks = ArrayList<Runnable>()
            feature.tileGeometry.multiPolygon?.let { multiPolygon ->
                tasks.add {
                    ctx.beginPath()
                    for (polygon in multiPolygon) {
                        for (ring in polygon) {
                            var viewCoord = ring.get(0)
                            ctx.moveTo(round(viewCoord.x), round(viewCoord.y))

                            for (i in 1 until ring.size) {
                                viewCoord = ring.get(i)
                                ctx.lineTo(round(viewCoord.x), round(viewCoord.y))
                            }
                        }
                    }
                }
                tasks.add(ctx::fill)
            }
            return tasks
        }

        override fun applyTo(ctx: Context2d) {
            setBaseStyle(ctx, myStyle)
        }
    }

    class LineSymbolizer internal constructor(private val myStyle: Style) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<Runnable> {
            val tasks = ArrayList<Runnable>()
            feature.tileGeometry.multiLineString?.let { multiLine ->
                tasks.add {
                    ctx.beginPath()
                    for (line in multiLine) {
                        var viewCoord = line[0]
                        ctx.moveTo(viewCoord.x, viewCoord.y)

                        for (i in 1 until line.size) {
                            viewCoord = line[i]
                            ctx.lineTo(viewCoord.x, viewCoord.y)
                        }
                    }
                }
                tasks.add(ctx::stroke)
            }
            return tasks
        }

        override fun applyTo(ctx: Context2d) {
            myStyle.stroke?.let { color -> ctx.setStrokeColor(color.toCssColor()) }
            myStyle.strokeWidth?.let(ctx::setLineWidth)

            myStyle.lineCap?.let { lineCap -> ctx.setLineCap(stringToLineCap(lineCap)) }
            myStyle.lineJoin?.let { lineJoin -> ctx.setLineJoin(stringToLineJoin(lineJoin)) }
            myStyle.lineDash?.let { lineDash -> ctx.setLineDash(lineDash.toDoubleArray()) }
        }
    }

    class PointTextSymbolizer internal constructor(
        private val myStyle: Style,
        private val myLabelBounds: MutableList<DoubleRectangle>
    ) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<Runnable> {
            val tasks = ArrayList<Runnable>()
            feature.tileGeometry.multiPoint?.let { multiPoint ->
                getLabel(feature)?.let { label ->
                    tasks.add {
                        val wrapWidth = myStyle.wrapWidth

                        if (wrapWidth != null && wrapWidth != 0.0 && wrapWidth < ctx.measureText(label)) {
                            drawWrapText(ctx, multiPoint, label, wrapWidth)
                        } else {
                            drawTextFast(ctx, multiPoint, label)
                        }
                    }
                }
            }
            return tasks
        }

        private fun drawTextFast(ctx: Context2d, multiPoint: List<DoubleVector>, label: String) {
            val width = ctx.measureText(label)
            val height = myStyle.size ?: 10.0

            multiPoint.forEach { point ->
                val bbox = DoubleRectangle.span(
                    DoubleVector(
                        point.x - width / 2,
                        point.y - height / 2
                    ),
                    DoubleVector(
                        point.x + width / 2,
                        point.y + height / 2
                    )
                )

                if (!labelInBounds(bbox)) {
                    ctx.strokeText(label, point.x, point.y)
                    ctx.fillText(label, point.x, point.y)

                    myLabelBounds.add(bbox)
                }
            }
        }

        private fun drawWrapText(ctx: Context2d, multiPoint: List<DoubleVector>, label: String, wrapWidth: Double) {
            var width = wrapWidth
            var words: MutableList<String> = splitLabel(label)
            var next: MutableList<String> = ArrayList()
            val rows = ArrayList<String>()

            val height = myStyle.size ?: 10.0

            while (!words.isEmpty()) {
                while (ctx.measureText(words.joinToString(" ")) > width && words.size != 1) {
                    next.add(0, words.removeAt(words.size - 1))
                }

                if (words.size == 1 && ctx.measureText(words[0]) > width) {
                    rows.add(words[0])
                    width = ctx.measureText(words[0])
                } else {
                    rows.add(words.joinToString(" "))
                }

                words = next
                next = ArrayList()
            }

            multiPoint.forEach { point ->
                val bbox = DoubleRectangle.span(
                    DoubleVector(
                        point.x - width / 2,
                        point.y - height * rows.size / 2
                    ),
                    DoubleVector(
                        point.x + width / 2,
                        point.y + height * rows.size / 2
                    )
                )

                if (!labelInBounds(bbox)) {
                    for (i in rows.indices) {
                        val row = rows[i]
                        val y = bbox.origin.y + height / 2 + height * i
                        ctx.strokeText(row, point.x, y)
                        ctx.fillText(row, point.x, y)
                    }

                    myLabelBounds.add(bbox)
                }
            }
        }

        private fun labelInBounds(bbox: DoubleRectangle): Boolean {
            for (bounds in myLabelBounds) {
                if (bbox.intersects(bounds)) {
                    return true
                }
            }

            return false
        }

        private fun getLabel(feature: TileFeature): String? =
            when (val labelField = myStyle.labelField ?: LABEL) {
                SHORT -> feature.short
                LABEL -> feature.label
                else -> throw IllegalStateException("Unknown label field: $labelField")
            }

        override fun applyTo(ctx: Context2d) {
            val builder = FontBuilder()
            myStyle.fontStyle?.let(builder::setFontStyle)
            myStyle.size?.let(builder::setSize)
            myStyle.fontface?.let(builder::setFontFace)

            ctx.setFont(builder.build())
            ctx.setTextAlign(Context2d.TextAlign.CENTER)
            ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE)

            setBaseStyle(ctx, myStyle)
        }
    }

    class ShieldTextSymbolizer internal constructor(style: Style, labelBounds: List<DoubleRectangle>) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<Runnable> {
            return emptyList<Runnable>()
        }

        override fun applyTo(ctx: Context2d) {

        }
    }

    class LineTextSymbolizer internal constructor(style: Style, labelBounds: List<DoubleRectangle>) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<Runnable> {
            return emptyList<Runnable>()
        }

        override fun applyTo(ctx: Context2d) {

        }
    }

    class FontBuilder internal constructor() {
        private var myFontStyle: String? = null
        private var mySize: String? = null
        private var myFontFace: String? = null

        init {
            myFontStyle = ""
            mySize = ""
            myFontFace = ""
        }

        internal fun build(): String {
            return "$myFontStyle $mySize $myFontFace"
        }

        internal fun setFontStyle(fontStyle: String) {
            myFontStyle = fontStyle
        }

        fun setSize(size: Double?) {
            mySize = size!!.toString() + "px"
        }

        internal fun setFontFace(fontFace: String) {
            myFontFace = fontFace
        }
    }

    companion object {
        private const val BUTT = "butt"
        private const val ROUND = "round"
        private const val SQUARE = "square"
        private const val MITER = "miter"
        private const val BEVEL = "bevel"

        private const val LINE = "line"
        private const val POLYGON = "polygon"
        private const val POINT_TEXT = "point-text"
        private const val SHIELD_TEXT = "shield-text"
        private const val LINE_TEXT = "line-text"
        private const val SHORT = "short"
        private const val LABEL = "label"

        fun create(style: Style, labelBounds: MutableList<DoubleRectangle>): Symbolizer {
            return when (style.type) {
                LINE -> LineSymbolizer(style)
                POLYGON -> PolygonSymbolizer(style)
                POINT_TEXT -> PointTextSymbolizer(style, labelBounds)
                SHIELD_TEXT -> ShieldTextSymbolizer(style, labelBounds)
                LINE_TEXT -> LineTextSymbolizer(style, labelBounds)
                null -> error("Empty symbolizer type.")
                else -> error("Unknown symbolizer type.")
            }
        }

        fun stringToLineCap(cap: String): Context2d.LineCap {
            return when (cap) {
                BUTT -> Context2d.LineCap.BUTT
                ROUND -> Context2d.LineCap.ROUND
                SQUARE -> Context2d.LineCap.SQUARE
                else -> error("Unknown lineCap type: $cap")
            }
        }

        fun stringToLineJoin(join: String): Context2d.LineJoin {
            return when (join) {
                BEVEL -> Context2d.LineJoin.BEVEL
                ROUND -> Context2d.LineJoin.ROUND
                MITER -> Context2d.LineJoin.MITER
                else -> error("Unknown lineJoin type: $join")
            }
        }

        fun splitLabel(label: String): MutableList<String> {
            val splitted = ArrayList<String>()
            val characters = "-',.)!?"
            var lastIndex = 0

            for (i in 0 until label.length) {
                if (' ' == label[i]) {
                    if (lastIndex != i) {
                        splitted.add(label.substring(lastIndex, i))
                    }
                    lastIndex = i + 1
                } else if (characters.indexOf(label[i]) != -1) {
                    splitted.add(label.substring(lastIndex, i + 1))
                    lastIndex = i + 1
                }
            }

            if (lastIndex != label.length) {
                splitted.add(label.substring(lastIndex))
            }

            return splitted
        }

        fun setBaseStyle(ctx: Context2d, style: Style) {
            style.strokeWidth?.let(ctx::setLineWidth)
            style.fill?.let { color -> ctx.setFillColor(color.toCssColor()) }
            style.stroke?.let { color -> ctx.setStrokeColor(color.toCssColor()) }
        }
    }
}