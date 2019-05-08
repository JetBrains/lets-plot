package jetbrains.datalore.visualization.plot.gog.config.aes

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

object AesOptionConversion {
    private val CONVERTERS_MAP = TypedOptionConverterMap()

    fun <T> getConverter(aes: Aes<T>): (Any?) -> T? {
        return CONVERTERS_MAP[aes]
    }

    fun <T> apply(aes: Aes<T>, optionValue: Any): T? {
        val converter = getConverter(aes)
        return converter(optionValue)
    }

    fun <T> applyToList(aes: Aes<T>, optionValues: List<*>): List<T?> {
        val converter = getConverter(aes)
        val result = ArrayList<T?>()
        for (optionValue in optionValues) {
            result.add(converter(optionValue!!))
        }
        return result
    }

    /**
     * For tests
     */
    internal fun has(aes: Aes<*>): Boolean {
        return CONVERTERS_MAP.containsKey(aes)
    }
}
