/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transf

class SpecFinder {
    private var myKeys: List<String>

    constructor(vararg keys: String) {
        myKeys = listOf(*keys)
    }

    constructor(keys: List<String>) {
        myKeys = ArrayList(keys)
    }

    fun findSpecs(containerSpec: Map<*, *>): List<Map<*, *>> {
        return if (myKeys.isEmpty()) listOf(containerSpec) else findSpecs(myKeys[0], myKeys.subList(1, myKeys.size), containerSpec)

    }

    private fun findSpecs(key: String, otherKeys: List<String>, containerSpec: Map<*, *>): List<Map<*, *>> {
        if (containerSpec.containsKey(key)) {
            val `val` = containerSpec[key]
            if (`val` is Map<*, *>) {
                return if (otherKeys.isEmpty()) {
                    listOf(`val`)
                } else {
                    findSpecs(otherKeys[0], otherKeys.subList(1, otherKeys.size), `val`)
                }
                //} else if (!otherKeys.isEmpty() && val instanceof List) {
            } else if (`val` is List<*>) {
                if (otherKeys.isEmpty()) {
                    // return all features in list
                    val result = ArrayList<Map<*, *>>()
                    for (o in `val`) {
                        if (o is Map<*, *>) {
                            result.add(o)
                        }
                    }
                    return result
                } else {
                    return findSpecsInList(otherKeys[0], otherKeys.subList(1, otherKeys.size), `val`)
                }
            }
        }

        return emptyList()
    }

    private fun findSpecsInList(key: String, otherKeys: List<String>, list: List<*>): List<Map<*, *>> {
        val result = ArrayList<Map<*, *>>()
        for (elem in list) {
            if (elem is Map<*, *>) {
                result.addAll(findSpecs(key, otherKeys, elem))
            } else if (elem is List<*>) {
                result.addAll(findSpecsInList(key, otherKeys, elem))
            }
        }
        return result
    }
}
