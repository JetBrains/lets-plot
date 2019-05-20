package jetbrains.datalore.base.js.css.enumerables

enum class CssVisibility constructor(override val stringQualifier: String) : CssBaseValue {
    COLLAPSE("collapse"),
    HIDDEN("hidden"),
    VISIBLE("visible");
}
