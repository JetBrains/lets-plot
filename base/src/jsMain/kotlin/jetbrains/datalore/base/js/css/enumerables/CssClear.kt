package jetbrains.datalore.base.js.css.enumerables

enum class CssClear constructor(override val stringQualifier: String) : CssBaseValue {
    NONE("none"),
    BOTH("both"),
    LEFT("left"),
    RIGHT("right");
}
