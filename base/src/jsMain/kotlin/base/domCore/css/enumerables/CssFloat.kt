package jetbrains.datalore.base.domCore.css.enumerables

class CssFloat private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NONE = CssFloat("none")
        val LEFT = CssFloat("left")
        val RIGHT = CssFloat("right")
    }
}
