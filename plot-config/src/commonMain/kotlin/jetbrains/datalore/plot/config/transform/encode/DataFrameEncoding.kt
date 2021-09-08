/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform.encode

import jetbrains.datalore.plot.common.base64.BinaryUtil

object DataFrameEncoding {
    private const val DATA_SPEC_KEY = "__data_spec_encoded"

    fun isEncodedDataSpec(o: Any): Boolean {
        return if (o is Map<*, *>) {
            o.size == 1 && o.containsKey(DATA_SPEC_KEY)
        } else false
    }

    fun decode1(map: Map<String, *>): Map<String, List<*>> {
        require(isEncodedDataSpec(map)) { "Not an encoded data spec" }

        val encodedData = map[DATA_SPEC_KEY] as List<*>

        val varNames = encodedData[0] as List<*>
        val isNumeric = encodedData[1] as List<*>
        val seriesStart = 2

        val decoded = HashMap<String, List<*>>()
        for (i in varNames.indices) {
            val name = varNames[i] as String
            val numeric = isNumeric[i] as Boolean

            val o = encodedData[seriesStart + i]
            val v = when {
                numeric -> BinaryUtil.decodeList(o as String)
                else -> o as List<*>
            }
            decoded[name] = v
        }

        return decoded
    }
}
