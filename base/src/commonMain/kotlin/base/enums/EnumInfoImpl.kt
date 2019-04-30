package jetbrains.datalore.base.enums

import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableMap

class EnumInfoImpl<EnumT : Enum<EnumT>>(enumConstants: Array<EnumT>) : EnumInfo<EnumT> {

    private val myNormalizedValueMap: Map<String, EnumT>
    private val myOriginalNames: List<String>

    override val originalNames: List<String>
        get() = myOriginalNames

    private fun toNormalizedName(name: String): String {
        return name.toUpperCase()
    }

    init {
        val numConstants = enumConstants.size
        val valueMap = HashMap<String, EnumT>(numConstants)
        val originalNames = ArrayList<String>(numConstants)
        for (value in enumConstants) {
            val originalName = value.toString()
            originalNames.add(originalName)
            val normalizedName = toNormalizedName(originalName)
            val oldValue = valueMap.put(normalizedName, value)
            if (oldValue != null) {
                throw IllegalArgumentException("duplicate values: '$value', '$oldValue'")
            }
        }
        myOriginalNames = unmodifiableList(originalNames)
        myNormalizedValueMap = unmodifiableMap(valueMap)
    }

    override fun safeValueOf(name: String?, defaultValue: EnumT): EnumT {
        val value = safeValueOf(name)
        return value ?: defaultValue
    }

    override fun safeValueOf(name: String?): EnumT? {
        val result: EnumT?
        if (hasValue(name)) {
            result = myNormalizedValueMap[toNormalizedName(name!!)]
        } else {
            result = null
        }
        return result
    }

    override fun hasValue(name: String?): Boolean {
        return name != null && myNormalizedValueMap.containsKey(toNormalizedName(name))
    }

    override fun unsafeValueOf(name: String): EnumT {
        return safeValueOf(name) ?: throw IllegalArgumentException("name not found: '$name'")
    }

}
