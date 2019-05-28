package jetbrains.datalore.base.enums

object EnumInfoFactory {

    /**
     * @throws IllegalArgumentException if there are same enumConstant.toString() values (case insensitive) in the enum
     */
    inline fun <reified EnumT : Enum<EnumT>> createEnumInfo(): EnumInfo<EnumT> {
        return EnumInfoImpl(enumValues())
    }
}
