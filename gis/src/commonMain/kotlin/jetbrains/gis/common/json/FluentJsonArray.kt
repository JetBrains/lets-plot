package jetbrains.gis.common.json

import jetbrains.gis.common.json.JsonUtils.objectsStreamOf
import jetbrains.gis.common.json.JsonUtils.streamOf
import jetbrains.gis.geoprotocol.json.JsonArray
import jetbrains.gis.geoprotocol.json.JsonObject


class FluentJsonArray: FluentJsonValue {
    private val myArray: JsonArray

    constructor() {
        myArray = JsonArray()
    }

    constructor(array: JsonArray) {
        myArray = array
    }

    fun add(v: Any?): FluentJsonArray {
        myArray.add(v)
        return this
    }

    fun add(v: Double): FluentJsonArray {
        myArray.add(v)
        return this
    }

    fun addAll(values: List<FluentJsonValue>): FluentJsonArray {
        values.forEach { v -> myArray.add(v.get()) }
        return this
    }

    fun addAllValues(values: List<Any?>): FluentJsonArray {
        myArray.addAll(values)
        return this
    }

    fun addAll(vararg values: FluentJsonValue): FluentJsonArray {
        return addAll(listOf(*values))
    }

    fun stream(): List<Any?> {
        return streamOf(myArray)
    }

    fun objectStream(): List<JsonObject> {
        return objectsStreamOf(myArray)
    }

    fun fluentObjectStream(): List<FluentJsonObject> {
        return objectsStreamOf(myArray).map(::FluentJsonObject)
    }

    fun getDouble(index: Int): Double {
        return myArray[index] as Double
    }

    override fun get(): JsonArray {
        return myArray
    }
}

