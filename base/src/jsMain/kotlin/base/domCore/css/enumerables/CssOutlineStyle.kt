package jetbrains.datalore.base.domCore.css.enumerables

class CssOutlineStyle private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NONE = CssOutlineStyle("none")
        val DASHED = CssOutlineStyle("dashed")
        val DOTTED = CssOutlineStyle("dotted")
        val DOUBLE = CssOutlineStyle("double")
        val GROOVE = CssOutlineStyle("groove")
        val INSET = CssOutlineStyle("inset")
        val OUTSET = CssOutlineStyle("outset")
        val RIDGE = CssOutlineStyle("ridge")
        val SOLID = CssOutlineStyle("solid")
    }
}
