package jetbrains.datalore.base.js.css.enumerables

enum class CssOutlineStyle constructor(override val stringQualifier: String) : CssBaseValue {
    NONE("none"),
    DASHED("dashed"),
    DOTTED("dotted"),
    DOUBLE("double"),
    GROOVE("groove"),
    INSET("inset"),
    OUTSET("outset"),
    RIDGE("ridge"),
    SOLID("solid");
}
