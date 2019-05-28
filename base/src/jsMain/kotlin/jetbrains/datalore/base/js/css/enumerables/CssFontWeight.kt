package jetbrains.datalore.base.js.css.enumerables

enum class CssFontWeight constructor(override val stringQualifier: String) : CssBaseValue {
    NORMAL("normal"),
    BOLD("bold"),
    BOLDER("bolder"),
    LIGHTER("lighter");
}
