package jetbrains.datalore.base.enums

object EnumInfoFactory {

    /**
     * @throws IllegalArgumentException if there are same enumConstant.toString() values (case insensitive) in the enum
     */
    fun <EnumT : Enum<EnumT>> createEnumInfo(enumClass: Class<EnumT>): EnumInfo<EnumT> {
        return EnumInfoImpl(enumClass)
    }
}
