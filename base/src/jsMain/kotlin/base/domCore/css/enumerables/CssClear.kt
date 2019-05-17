package jetbrains.datalore.base.domCore.css.enumerables

class CssClear private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NONE = CssClear("none")
        val BOTH = CssClear("both")
        val LEFT = CssClear("left")
        val RIGHT = CssClear("right")
    }
}
