package jetbrains.datalore.visualization.base.svg

class SvgAttributeSpec<ValueT> private constructor(val name: String, val namespaceUri: String?) {

    fun hasNamespace(): Boolean {
        return namespaceUri != null
    }

    override fun toString(): String {
        return name
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is SvgAttributeSpec<*>) return false

        val that = o as SvgAttributeSpec<*>?

        return if (name != that!!.name) false else true

    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        fun <ValueT> createSpec(name: String): SvgAttributeSpec<ValueT> {
            return SvgAttributeSpec(name, null)
        }

        fun <ValueT> createSpecNS(name: String, prefix: String, namespaceUri: String): SvgAttributeSpec<ValueT> {
            return SvgAttributeSpec("$prefix:$name", namespaceUri)
        }
    }
}