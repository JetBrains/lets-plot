package jetbrains.datalore.base.js.css.enumerables

enum class CssVerticalAlign constructor(override val stringQualifier: String) : CssBaseValue {
    BASELINE("baseline"),
    SUB("sub"),
    SUPER("super"),
    TOP("top"),
    TEXT_TOP("text_top"),
    MIDDLE("middle"),
    BOTTOM("bottom"),
    TEXT_BOTTOM("text_bottom");
}
