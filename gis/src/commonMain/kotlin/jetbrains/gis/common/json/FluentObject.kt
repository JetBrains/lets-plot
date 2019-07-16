package jetbrains.gis.common.json

import jetbrains.datalore.base.function.Consumer


class FluentObject : FluentValue {
    private val myObj: MutableMap<String, Any?>

    constructor(obj: Map<*, *>) {
        myObj = (obj as Map<String, Any?>).toMutableMap()
    }

    constructor() {
        myObj = HashMap()

   }
    private fun getArr(key: String) = myObj[key] as Arr
    private fun getObj(key: String) = myObj[key] as Obj

    override fun get(): Obj = myObj
    operator fun contains(key: String) = myObj.containsKey(key)
    private fun containsNotNull(key: String) = contains(key) && myObj[key] != null

    fun put(key: String, v: FluentValue?) = apply { myObj[key] = v?.get() }
    fun put(key: String, v: String?) = apply { myObj[key] = v }
    fun put(key: String, v: Number?) = apply { myObj[key] = v }
    fun put(key: String, v: Boolean?) = apply { myObj[key] = v }
    fun <T : Enum<T>> put(key: String, v: T?) = apply { myObj[key] = v?.let { formatEnum(it) } }

    fun getInt(key: String) = myObj[key] as Int
    fun getDouble(key: String) = myObj.getDouble(key)
    fun getBoolean(key: String) = myObj[key] as Boolean
    fun getString(key: String) = myObj[key] as String
    fun getStrings(key: String) = getArr(key).map { getAsString(it) }
    fun <T : Enum<T>> getEnum(key: String, enumValues: Array<T>) = parseEnum(myObj[key] as String, enumValues)
    fun getArray(key: String) = FluentArray(getArr(key))
    fun getObject(key: String) = FluentObject(getObj(key))

    fun getInt(key: String, processor: (Int) -> Unit) = apply { processor(getInt(key)) }
    fun getDouble(key: String, processor: (Double) -> Unit) = apply { processor(getDouble(key)) }
    fun getBoolean(key: String, processor: (Boolean) -> Unit) = apply { processor(getBoolean(key)) }
    fun getString(key: String, processor: (String) -> Unit) = apply { processor(getString(key)) }
    fun getStrings(key: String, processor: (List<String?>) -> Unit) = apply { processor(getStrings(key)) }
    fun <T : Enum<T>> getEnum(key: String, processor: (T) -> Unit, enumValues: Array<T>) = apply { processor(getEnum(key, enumValues)) }
    fun getArray(key: String, processor: (FluentArray) -> Unit) = apply { processor(getArray(key)) }
    fun getObject(key: String, processor: (FluentObject) -> Unit) = apply { processor(getObject(key)) }

    fun putRemovable(key: String, v: FluentValue?) = apply { v?.let { put(key, it) } }
    fun <T : Enum<T>> putRemovable(key: String, v: T?) = apply { v?.let { put(key, it) } }

    fun forEntries(consumer: (String, Any?) -> Unit) = apply { myObj.keys.forEach { consumer(it, myObj[it]) } }
    fun forObjEntries(consumer: (String, Obj) -> Unit) = apply { myObj.keys.forEach { consumer(it, myObj[it] as Obj) } }
    fun forArrEntries(consumer: (String, Arr) -> Unit) = apply { myObj.keys.forEach { consumer(it, myObj[it] as Arr) } }
    fun accept(consumer: (FluentObject) -> Unit) = apply { consumer(this) }
    fun forStrings(key: String, processor: (String?) -> Unit) = apply { myObj.getArr(key).map(::getAsString).forEach(processor) }


    fun getExistingDouble(key: String, processor: Consumer<Double>) = apply {
        if (containsNotNull(key)) {
            getDouble(key, processor)
        }
    }

    fun getOptionalStrings(key: String, processor: (List<String?>?) -> Unit) = apply {
        if (containsNotNull(key)) {
            processor(getStrings(key))
        } else {
            processor(null)
        }
    }

    fun getExistingString(key: String, processor: (String) -> Unit) = apply {
        if (containsNotNull(key)) {
            getString(key, processor)
        }
    }

    fun forExistingStrings(key: String, processor: (String) -> Unit) = apply {
        if (containsNotNull(key)) {
            forStrings(key) { processor(it!!) }
        }
    }

    fun getExistingObject(key: String, processor: (FluentObject) -> Unit) = apply {
        if (containsNotNull(key)) {
            val obj = getObject(key)
            if (obj.myObj.keys.isNotEmpty()) {
                processor(obj)
            }
        }
    }

    fun getExistingArray(key: String, processor: (FluentArray) -> Unit) = apply {
        if (containsNotNull(key)) {
            processor(getArray(key))
        }
    }

    fun forObjects(key: String, processor: (FluentObject) -> Unit) = apply {
        getArray(key).fluentObjectStream().forEach(processor)
    }


    fun getOptionalInt(key: String, processor: (Int?) -> Unit) = apply {
        if (containsNotNull(key)) {
            processor(getInt(key))
        } else {
            processor(null)
        }
    }

    fun getIntOrDefault(key: String, processor: (Int) -> Unit, defaultValue: Int) = apply {
        if (containsNotNull(key)) {
            processor(getInt(key))
        } else {
            processor(defaultValue)
        }
    }



    fun <T : Enum<T>> forEnums(key: String, processor: (T) -> Unit, enumValues: Array<T>) = apply {
        getArr(key).forEach { processor(parseEnum(it as String, enumValues)) }
    }

    fun <T : Enum<T>> getOptionalEnum(key: String, processor: (T?) -> Unit, enumValues: Array<T>) = apply {
        when {
            containsNotNull(key) -> processor(getEnum(key, enumValues))
            else -> processor(null)
        }
    }

    private fun toJsonType(v: Any?): Any? {
        if (v == null) {
            return null
        }

        return when (v) {
            null -> null
            is Boolean,
            is String,
            is Number,
            is Map<*, *> -> v
            is Collection<*> -> v.toList()
            is FluentValue -> v.get()
            else -> throw IllegalArgumentException("Not supported type: ${v.toString()}")
        }
    }

    //    fun <T : Enum<T>> getExistingEnum(key: String, processor: Consumer<T>, enumValues: Array<T>): FluentObject {
//        if (containsNotNull(key)) {
//            processor.accept(getEnum(key, enumValues))
//        }
//        return this
//    }
//
//    fun forKeys(consumer: Consumer<String>): FluentObject {
//        myObj.getKeys().forEach(consumer)
//        return this
//    }
//




//    operator fun get(key: String, processor: Consumer<JsonValue>): FluentObject {
//        processor.accept(myObj.get(key))
//        return this
//    }
//
//    fun getExisting(key: String, processor: Consumer<JsonValue>): FluentObject {
//        if (containsNotNull(key)) {
//            processor.accept(myObj.get(key))
//        }
//        return this
//    }
//

}
