package jetbrains.datalore.base.js.css.enumerables

enum class CssDisplay constructor(override val stringQualifier: String) : CssBaseValue {
    DEFAULT("default"),
    NONE("none"),
    BLOCK("block"),
    FLEX("flex"),
    GRID("grid"),
    INLINE_BLOCK("inline-block");
}
