package jetbrains.datalore.base.domCore.css.enumerables

class CssAlignItem private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val DEFAULT = CssAlignItem("default")
        val CENTER = CssAlignItem("center")
        val STRETCH = CssAlignItem("stretch")
        val FLEX_START = CssAlignItem("flex-start")
        val FLEX_END = CssAlignItem("flex-end")
    }
}
