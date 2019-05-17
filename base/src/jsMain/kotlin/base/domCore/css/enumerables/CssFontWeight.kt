package jetbrains.datalore.base.domCore.css.enumerables

class CssFontWeight private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NORMAL = CssFontWeight("normal")
        val BOLD = CssFontWeight("bold")
        val BOLDER = CssFontWeight("bolder")
        val LIGHTER = CssFontWeight("lighter")
    }
}
