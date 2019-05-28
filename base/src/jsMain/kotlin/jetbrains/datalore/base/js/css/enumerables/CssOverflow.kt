package jetbrains.datalore.base.js.css.enumerables

enum class CssOverflow private constructor(override val stringQualifier: String) : CssBaseValue {
    VISIBLE("visible"),
    HIDDEN("hidden"),
    SCROLL("scroll"),
    AUTO("auto"),
    INITIAL("initial"),
    INHERIT("inherit");

    companion object {
        fun parse(value: String): CssOverflow? {
            return parse(value, values())
        }
    }
}
