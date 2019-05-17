package jetbrains.datalore.base.domCore.css.enumerables

class CssPosition private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val ABSOLUTE = CssPosition("absolute")
        val FIXED = CssPosition("fixed")
        val RELATIVE = CssPosition("relative")
        val STATIC = CssPosition("static")
        val STICKY = CssPosition("sticky")

        private val ALL_VALUES = listOf(
                ABSOLUTE, FIXED, RELATIVE, STATIC, STICKY
        )

        fun parse(value: String): CssPosition? {
            return parse(value, ALL_VALUES)
        }
    }
}
