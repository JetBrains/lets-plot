package jetbrains.datalore.base.js.css.enumerables

enum class CssAlignItem constructor(override val stringQualifier: String) : CssBaseValue {
    DEFAULT("default"),
    CENTER("center"),
    STRETCH("stretch"),
    FLEX_START("flex-start"),
    FLEX_END("flex-end");
}
