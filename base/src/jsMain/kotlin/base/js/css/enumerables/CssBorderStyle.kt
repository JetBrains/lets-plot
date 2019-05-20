package jetbrains.datalore.base.js.css.enumerables

enum class CssBorderStyle constructor(override val stringQualifier: String) : CssBaseValue {
    NONE("none"),
    DOTTED("dotted"),
    DASHED("dashed"),
    HIDDEN("hidden"),
    SOLID("solid");
}
