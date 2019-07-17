package jetbrains.datalore.base.jsObject

import mu.KotlinLogging

private val LOG = KotlinLogging.logger("JsObjectSupportJs")

fun jsonToMap(v: dynamic): MutableMap<String, Any?> = JsonToMap().handleObject(v)
fun plotToMap(v: dynamic): MutableMap<String, Any> = PlotToMap().handleObject(v) as MutableMap<String, Any>

internal open class JsonToMap(private val allowNullProp: Boolean = true) {

    internal fun handleObject(v: dynamic): MutableMap<String, Any?> {
        return js("Object").entries(v)
            .unsafeCast<Array<Array<*>>>()
            .mapNotNull { (k, v) -> handleProperty(k as String, v) }
            .toMap(HashMap())
    }

    protected open fun handleArray(v: Array<*>) = v.map { handleValue(it) }

    private fun handleProperty(k: String, v: Any?): Pair<String, Any?>? {
        val prop = handleValue(v)
        return when {
            prop != null || allowNullProp -> k to prop
            else -> null
        }

        // prop inside the when statement breaks tests
        //return when(val prop = handleValue(v)) {
        //    prop != null || allowNullProp -> k to prop
        //    else -> k to prop
        //}

    }

    private fun handleValue(v: Any?): Any? {
        return when (v) {
            is String, Boolean, null -> v
            is Number -> v.toDouble()
            is Array<*> -> handleArray(v)
            else -> handleObject(v)
        }
    }
}

/**
 * Copies all object's properties to hash map recursively with exception of
 * arrays containing only simple values (str,number,boolean,null).
 * 'simple' arrays are wrapped in immutable lists (not copied)
 */
internal class PlotToMap : JsonToMap(allowNullProp = false) {
    override fun handleArray(v: Array<*>) =
        when {
            v.all { isPrimitiveOrNull(it) } -> listOf(*v) // do not copy data vectors
            else -> super.handleArray(v)
        }

    private fun isPrimitiveOrNull(v: dynamic) = when (v) {
        v is Number, String, Boolean, null -> true
        else -> false
    }
}
