package jetbrains.datalore.mapper.core

/**
 * Typed key object for putting user data into [MappingContext]
 */
class MappingContextProperty<ValueT>(private val myName: String) {

    override fun toString(): String {
        return "MappingContextProperty[$myName]"
    }
}