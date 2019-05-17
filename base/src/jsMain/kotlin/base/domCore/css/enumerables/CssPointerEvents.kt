package jetbrains.datalore.base.domCore.css.enumerables

class CssPointerEvents private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val NONE = CssPointerEvents("none")
        val AUTO = CssPointerEvents("auto")
    }
}
