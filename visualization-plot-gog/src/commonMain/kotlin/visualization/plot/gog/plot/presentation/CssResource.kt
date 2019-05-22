package visualization.plot.gog.plot.presentation

class CssResourceBuilder {
    private val selectorsMap = mutableMapOf<Selector, MutableMap<StyleType, Any>>()

    fun add(selectorBuilder: SelectorBuilder): CssResourceBuilder {
        val selectorsAndStyles = selectorBuilder.build()
        val selector = selectorsAndStyles.first
        if (!selectorsMap.containsKey(selector)) {
            selectorsMap[selector] = mutableMapOf()
        }
        for (style in selectorsAndStyles.second) {
            selectorsMap[selector]!![style.key] = style.value
        }
        return this
    }

    fun build(): CssResource {
        return CssResource(selectorsMap)
    }
}

class SelectorBuilder {
    constructor(selectorName: Any) {
        this.selectorNames.add(selectorName)
    }

    constructor(selectorNames: List<Any>) {
        for (selectorName in selectorNames) {
            this.selectorNames.add(selectorName)
        }
    }

    private val selectorNames = mutableListOf<Any>()
    private val styleMap = mutableMapOf<StyleType, Any>()
    private var innerSelector: SelectorBuilder? = null

    fun innerSelector(selectorName: Any): SelectorBuilder {
        innerSelector = SelectorBuilder(selectorName)

        return this
    }

    fun innerSelector(selectorNames: List<Any>): SelectorBuilder {
        innerSelector = SelectorBuilder(selectorNames)

        return this
    }

    fun fontSize(size: Int, measure: SizeMeasure): SelectorBuilder {
        styleMap[StyleType.FONT_SIZE] = FontSizeValue(size, measure)

        return this
    }

    fun fontFamily(fontValue: String): SelectorBuilder {
        styleMap[StyleType.FONT_FAMILY] = fontValue

        return this
    }

    fun fill(hexColor: String): SelectorBuilder {
        styleMap[StyleType.FILL] = hexColor

        return this
    }

    fun cursor(value: CursorValue): SelectorBuilder {
        styleMap[StyleType.CURSOR] = value

        return this
    }

    fun pointerEvents(value: PointerEventsValue): SelectorBuilder {
        styleMap[StyleType.POINTER_EVENTS] = value

        return this
    }

    fun opacity(value: Float): SelectorBuilder {
        styleMap[StyleType.OPACITY] = value

        return this
    }

    fun shapeRendering(value: ShapeRenderingValue): SelectorBuilder {
        styleMap[StyleType.SHAPE_RENDERING] = value

        return this
    }

    fun fillOpacity(value: Float): SelectorBuilder {
        styleMap[StyleType.FILL_OPACITY] = value

        return this
    }

    fun build(): Pair<Selector, Map<StyleType, Any>> {
        val selector = Selector(selectorNames, innerSelector?.build()?.first)
        return Pair(selector, styleMap)
    }
}


class CssResource(val selectorMap: Map<Selector, Map<StyleType, Any>>) {
    override fun toString(): String {
        val sb = StringBuilder()

        for (selector in selectorMap.keys) {
            sb.append(selector.toString()).append(" {\n")
            sb.append(styleToString(selectorMap.getValue(selector)))
            sb.append("}\n")
        }
        return sb.toString()
    }

    private fun styleToString(styleMap: Map<StyleType, Any>): String {
        val sb = StringBuilder()

        for (styleType in styleMap.keys) {
            sb.append("\t")
            sb.append(styleType.str).append(": ").append(styleMap[styleType].toString()).append(";\n")
        }

        return sb.toString()
    }
}

class Selector(val selectors: List<Any>, val innerSelector: Selector? = null) {
    override fun toString(): String {
        val sb = StringBuilder()

        for (selector in selectors) {
            if (selector is String) {
                sb.append(".$selector")
            }
            else if (selector is SelectorType) {
                sb.append(selector.str)
            }
        }

        if (innerSelector != null) {
            sb.append(" ").append(innerSelector.toString())
        }

        return sb.toString()
    }
}

class FontSizeValue(val size: Int, val measure: SizeMeasure) {
    override fun toString() = "$size${measure.str}"
}

enum class SelectorType(val str: String) {
    TEXT("text"),
    LINE("line"),
    CUSTOM("")
}

enum class StyleType(val str: String) {
    FONT_FAMILY("font-family"),
    FONT_SIZE("font-size"),
    FILL("fill"),
    CURSOR("cursor"),
    POINTER_EVENTS("pointer-events"),
    OPACITY("opacity"),
    SHAPE_RENDERING("shape-rendering"),
    FILL_OPACITY("fill-opacity")
}

enum class SizeMeasure(val str: String) {
    PX("px");
    override fun toString() = str
}

enum class CursorValue(val str: String) {
    CROSSHAIR("crosshair");
    override fun toString() = str
}

enum class ShapeRenderingValue(val str: String) {
    CRISPEDGES("crispedges");
    override fun toString() = str
}

enum class PointerEventsValue(val str: String) {
    NONE("none");
    override fun toString() = str
}