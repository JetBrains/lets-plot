package jetbrains.datalore.base.domCore.css.enumerables

class CssVisibility private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val COLLAPSE = CssVisibility("collapse")
        val HIDDEN = CssVisibility("hidden")
        val VISIBLE = CssVisibility("visible")
    }
}
