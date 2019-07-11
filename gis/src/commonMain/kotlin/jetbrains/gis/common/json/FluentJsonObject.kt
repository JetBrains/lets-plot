package jetbrains.gis.common.json

import jetbrains.gis.common.json.JsonUtils.formatEnum


class FluentJsonObject : FluentJsonValue {
    private val myObj: JsonObject

    constructor(obj: JsonObject) {
        myObj = obj
    }

    constructor() {
        myObj = JsonObject()
    }

//    fun put(key: String, v: String?) = apply { myObj[key] = v }
//
//    fun put(key: String, v: Double) = apply { myObj[key] = v }
//
//    fun put(key: String, v: Int) = apply { myObj[key] = v }
//
//    fun put(key: String, v: Boolean) = apply { myObj[key] = v }

    fun <T : Enum<T>> put(key: String, v: T?) = apply {myObj[key] = v?.let { formatEnum(it) }}
//
//    fun <T : Enum<T>> put(key: String, v: Optional<T>): FluentJsonObject {
//        return put<T>(key, v.orElse(null))
//    }
//
//    fun put(key: String, v: FluentJsonValue?) = apply { myObj[key] = v?.get() }
//
    fun put(key: String, v: Any?) = apply { myObj[key] = v }
//
    fun putRemovable(key: String, v: FluentJsonValue?) = apply {
        if (v != null) {
            put(key, v)
        }
        return this
    }

    fun putRemovable(key: String, v: Any?): FluentJsonObject {
        if (v != null) {
            put(key, v)
        }
        return this
    }

    fun <T : Enum<T>> putRemovable(key: String, v: T?): FluentJsonObject {
        if (v != null) {
            put(key, v)
        }

        return this
    }
//
//    operator fun get(key: String, processor: Consumer<JsonValue>): FluentJsonObject {
//        processor.accept(myObj.get(key))
//        return this
//    }
//
//    fun getExisting(key: String, processor: Consumer<JsonValue>): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(myObj.get(key))
//        }
//        return this
//    }
//
//    fun getDouble(key: String): Double {
//        return myObj.getDouble(key)
//    }
//
//    fun getDouble(key: String, processor: Consumer<Double>): FluentJsonObject {
//        processor.accept(getDouble(key))
//        return this
//    }
//
//    fun getExistingDouble(key: String, processor: Consumer<Double>): FluentJsonObject {
//        return if (containsNotNull(key)) {
//            getDouble(key, processor)
//        } else this
//    }
//
//    fun getBoolean(key: String): Boolean {
//        return myObj.getBoolean(key)
//    }
//
//    fun getBoolean(key: String, processor: Consumer<Boolean>): FluentJsonObject {
//        processor.accept(getBoolean(key))
//        return this
//    }
//
//    fun getString(key: String): String {
//        return myObj.getString(key)
//    }
//
//    fun getString(key: String, processor: Consumer<String>): FluentJsonObject {
//        processor.accept(getString(key))
//        return this
//    }
//
//    fun getStrings(key: String): List<String> {
//        return FluentJsonArray(myObj.getArray(key))
//            .stream()
//            .map(???({ JsonUtils.getAsString() }))
//        .collect(Collectors.toList())
//    }
//
//    fun getStrings(key: String, processor: Consumer<List<String>>): FluentJsonObject {
//        processor.accept(getStrings(key))
//        return this
//    }
//
//    fun getOptionalStrings(key: String, processor: Consumer<Optional<List<String>>>): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(Optional.of(getStrings(key)))
//        } else {
//            processor.accept(Optional.empty())
//        }
//        return this
//    }
//
//    fun getExistingString(key: String, processor: Consumer<String>): FluentJsonObject {
//        return if (containsNotNull(key)) {
//            getString(key, processor)
//        } else this
//    }
//
//    fun forStrings(key: String, processor: Consumer<String>): FluentJsonObject {
//        FluentJsonArray(myObj.getArray(key)).stream().map(???({ JsonUtils.getAsString() })).forEach(processor)
//        return this
//    }
//
//    fun forExistingStrings(key: String, processor: Consumer<String>): FluentJsonObject {
//        return if (containsNotNull(key)) {
//            forStrings(key, processor)
//        } else this
//    }
//
//    fun getArray(key: String, processor: Consumer<FluentJsonArray>): FluentJsonObject {
//        processor.accept(FluentJsonArray(myObj.getArray(key)))
//        return this
//    }
//
//    fun getArray(key: String): FluentJsonArray {
//        return FluentJsonArray(myObj.getArray(key))
//    }
//
//    fun getObject(key: String): FluentJsonObject {
//        return FluentJsonObject(myObj.get(key))
//    }
//
//    fun getObject(key: String, processor: Consumer<FluentJsonObject>): FluentJsonObject {
//        processor.accept(getObject(key))
//        return this
//    }
//
//    fun getExistingObject(key: String, processor: Consumer<FluentJsonObject>): FluentJsonObject {
//        if (containsNotNull(key)) {
//            val `object` = getObject(key)
//            if (!`object`.myObj.getKeys().isEmpty()) {
//                processor.accept(`object`)
//            }
//        }
//
//        return this
//    }
//
//    fun getExistingArray(key: String, processor: Consumer<FluentJsonArray>): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(getArray(key))
//        }
//
//        return this
//    }
//
//    fun forObjects(key: String, processor: Consumer<FluentJsonObject>): FluentJsonObject {
//        FluentJsonArray(myObj.getArray(key)).fluentObjectStream().forEach(processor)
//        return this
//    }
//
//    fun getInt(key: String): Int {
//        return myObj.getInt(key)
//    }
//
//    fun getInt(key: String, processor: Consumer<Int>): FluentJsonObject {
//        processor.accept(getInt(key))
//        return this
//    }
//
//    fun getOptionalInt(key: String, processor: Consumer<Optional<Int>>): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(Optional.of(getInt(key)))
//        } else {
//            processor.accept(Optional.empty())
//        }
//        return this
//    }
//
//    fun getIntOrDefault(key: String, processor: Consumer<Int>, defaultValue: Int): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(getInt(key))
//        } else {
//            processor.accept(defaultValue)
//        }
//        return this
//    }
//
//    fun <T : Enum<T>> getEnum(key: String, enumValues: Array<T>): T {
//        return parseEnum(myObj.getString(key), enumValues)
//    }
//
//    fun <T : Enum<T>> getEnum(key: String, processor: Consumer<T>, enumValues: Array<T>): FluentJsonObject {
//        processor.accept(getEnum(key, enumValues))
//        return this
//    }
//
//    fun <T : Enum<T>> forEnums(key: String, processor: Consumer<T>, enumValues: Array<T>): FluentJsonObject {
//        FluentJsonArray(myObj.getArray(key))
//            .stream()
//            .map(???({ JsonUtils.getAsString() }))
//        .forEach { enumValue -> processor.accept(parseEnum(enumValue, enumValues)) }
//        return this
//    }
//
//    fun <T : Enum<T>> getOptionalEnum(
//        key: String,
//        processor: Consumer<Optional<T>>,
//        enumValues: Array<T>
//    ): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(Optional.of(getEnum(key, enumValues)))
//        } else {
//            processor.accept(Optional.empty())
//        }
//        return this
//    }
//
//    fun <T : Enum<T>> getExistingEnum(key: String, processor: Consumer<T>, enumValues: Array<T>): FluentJsonObject {
//        if (containsNotNull(key)) {
//            processor.accept(getEnum(key, enumValues))
//        }
//        return this
//    }
//
//    fun forKeys(consumer: Consumer<String>): FluentJsonObject {
//        myObj.getKeys().forEach(consumer)
//        return this
//    }
//
//    fun forEntries(consumer: BiConsumer<String, JsonValue>): FluentJsonObject {
//        myObj.getKeys().forEach { key -> consumer.accept(key, myObj.get(key)) }
//        return this
//    }
//
//    fun accept(consumer: Consumer<FluentJsonObject>): FluentJsonObject {
//        consumer.accept(this)
//        return this
//    }
//
//    operator fun contains(key: String): Boolean {
//        return myObj.getKeys().contains(key)
//    }
//
//    private fun containsNotNull(key: String): Boolean {
//        return contains(key) && myObj.get(key) !is JsonNull
//    }
//
    override fun get(): JsonObject {
        return myObj
    }
}
