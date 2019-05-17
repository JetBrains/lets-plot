package jetbrains.datalore.base.domCore.css.enumerables

class CssBorderStyle private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NONE = CssBorderStyle("none")
        val DOTTED = CssBorderStyle("dotted")
        val DASHED = CssBorderStyle("dashed")
        val HIDDEN = CssBorderStyle("hidden")
        val SOLID = CssBorderStyle("solid")
    }

}
