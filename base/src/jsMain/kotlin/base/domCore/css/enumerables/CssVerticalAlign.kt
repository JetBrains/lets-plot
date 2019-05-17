package jetbrains.datalore.base.domCore.css.enumerables

class CssVerticalAlign private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val BASELINE = CssVerticalAlign("")
        val SUB = CssVerticalAlign("")
        val SUPER = CssVerticalAlign("")
        val TOP = CssVerticalAlign("")
        val TEXT_TOP = CssVerticalAlign("")
        val MIDDLE = CssVerticalAlign("")
        val BOTTOM = CssVerticalAlign("")
        val TEXT_BOTTOM = CssVerticalAlign("")
    }
}
