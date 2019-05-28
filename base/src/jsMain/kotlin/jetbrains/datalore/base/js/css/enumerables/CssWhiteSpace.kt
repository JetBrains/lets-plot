package jetbrains.datalore.base.js.css.enumerables

enum class CssWhiteSpace constructor(override val stringQualifier: String) : CssBaseValue {
    NORMAL("normal"),
    NOWRAP("nowrap"),
    PRE("pre"),
    PRE_LINE("pre-line"),
    PRE_WRAP("pre-wrap");
}
