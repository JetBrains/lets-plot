package jetbrains.datalore.base.domCore.css.enumerables

class CssFontStyle private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NORMAL = CssFontStyle("normal")
        val ITALIC = CssFontStyle("italic")
    }
}
