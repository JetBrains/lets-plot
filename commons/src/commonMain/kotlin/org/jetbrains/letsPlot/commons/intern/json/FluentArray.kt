/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json


class FluentArray: FluentValue {
    private val myArray: ArrayList<Any?>

    constructor() {
        myArray = ArrayList<Any?>()
    }

    constructor(array: List<Any?>) {
        myArray = ArrayList<Any?>(array)
    }

    fun getDouble(index: Int) = myArray[index] as Double

    fun add(v: String?) = apply { myArray.add(v) }
    fun add(v: Double?) = apply { myArray.add(v) }
    fun addStrings(values: List<String?>) = apply { myArray.addAll(values) }
    fun addAll(values: List<FluentValue>) = apply { values.forEach { v -> myArray.add(v.get()) } }
    fun addAll(vararg values: FluentValue) = apply { addAll(listOf(*values)) }

    fun stream() = streamOf(myArray)
    fun objectStream() = objectsStreamOf(myArray)
    fun fluentObjectStream() = objectsStreamOf(myArray).map(::FluentObject)

    override fun get() = myArray
}

