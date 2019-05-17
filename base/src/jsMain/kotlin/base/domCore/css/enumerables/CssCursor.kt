package jetbrains.datalore.base.domCore.css.enumerables

class CssCursor private constructor(representation: String) : CssBaseValue(representation) {
    companion object {

        val DEFAULT = CssCursor("default")
        val POINTER = CssCursor("pointer")
    }
}
