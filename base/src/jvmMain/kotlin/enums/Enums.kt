package jetbrains.datalore.base.enums

object Enums {
    /**
     * Value of method for enums which takes into account toString() instead of saved generated name
     */
    fun <EnumT : Enum<EnumT>> valueOf(cls: Class<EnumT>, name: String): EnumT {
        for (e in cls.enumConstants) {
            if (name == e.toString()) {
                return e
            }
        }

        throw IllegalArgumentException(name)
    }
}