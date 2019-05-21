package visualization.plot.gog.plot.presentation

class CssResource {
    private val selectors = mutableListOf<Selector>()

    fun addSelector(selector: Selector): CssResource {
        selectors.add(selector)

        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (selector in selectors) {
            sb.append(selector.toString()).append("\n\n")
        }

        return sb.toString()
    }
}

class Selector {
    constructor(containerName: String) {
        selectorName = containerName
    }

    constructor(selectors: List<String>) {
        selectorName =  selectors.joinToString(separator = " ")
    }

    private val selectorName: String
    private val styles = mutableListOf<Style>()

    fun addStyle(styleType: StyleType, value: Any, measure: String = "") : Selector {
        styles.add(Style(styleType, value, measure))

        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(selectorName).append(" { ")
        for (style in styles) {
            sb.append(style.toString()).append("; ")
        }
        sb.append(" }")

        return sb.toString()
    }

    class Style(private val type: StyleType, private val value: Any, private val measure: String = "") {
        override fun toString(): String  {
            val styleTypeString = type.str
            return "$styleTypeString: $value$measure"
        }
    }
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