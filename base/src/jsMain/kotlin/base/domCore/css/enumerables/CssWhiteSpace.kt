package jetbrains.datalore.base.domCore.css.enumerables

class CssWhiteSpace private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NORMAL = CssWhiteSpace("normal")
        val NOWRAP = CssWhiteSpace("nowrap")
        val PRE = CssWhiteSpace("pre")
        val PRE_LINE = CssWhiteSpace("pre-line")
        val PRE_WRAP = CssWhiteSpace("pre-wrap")
    }
}
