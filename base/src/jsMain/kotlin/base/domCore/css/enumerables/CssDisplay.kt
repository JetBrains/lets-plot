package jetbrains.datalore.base.domCore.css.enumerables

class CssDisplay private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val DEFAULT = CssDisplay("default")
        val NONE = CssDisplay("none")
        val BLOCK = CssDisplay("block")
        val FLEX = CssDisplay("flex")
        val GRID = CssDisplay("grid")
        val INLINE_BLOCK = CssDisplay("inline-block")
    }
}
