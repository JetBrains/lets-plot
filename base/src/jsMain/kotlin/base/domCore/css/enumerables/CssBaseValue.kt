package jetbrains.datalore.base.domCore.css.enumerables

import jetbrains.datalore.base.domCore.css.CssUnitQualifier

abstract class CssBaseValue protected constructor(override val stringQualifier: String) : CssUnitQualifier {
    companion object {

        internal fun <TypeT : CssBaseValue> parse(str: String, values: Collection<TypeT>): TypeT? {
            for (value in values) {
                if (value.stringQualifier.equals(str, ignoreCase = true)) {
                    return value
                }
            }
            return null
        }
    }
}
