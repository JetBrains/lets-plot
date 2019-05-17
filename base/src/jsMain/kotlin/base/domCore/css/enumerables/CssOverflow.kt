package jetbrains.datalore.base.domCore.css.enumerables

class CssOverflow private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val VISIBLE = CssOverflow("visible")
        val HIDDEN = CssOverflow("hidden")
        val SCROLL = CssOverflow("scroll")
        val AUTO = CssOverflow("auto")
        val INITIAL = CssOverflow("initial")
        val INHERIT = CssOverflow("inherit")

        private val ALL_VALUES = listOf(
                VISIBLE, HIDDEN, SCROLL, AUTO, INITIAL, INHERIT
        )

        fun parse(value: String): CssOverflow? {
            return parse(value, ALL_VALUES)
        }
    }
}
