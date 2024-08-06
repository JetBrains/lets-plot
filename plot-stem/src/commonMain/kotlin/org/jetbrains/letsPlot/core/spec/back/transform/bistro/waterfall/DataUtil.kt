/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

internal object DataUtil {
    fun groupBy(
        data: Map<String, List<*>>,
        group: String?
    ): List<Map<String, List<*>>> {
        return if (group != null && group in data.keys) {
            val groupValues = data.getValue(group)
            val result = mutableListOf<Map<String, List<*>>>()
            for (groupValue in groupValues.distinct()) {
                val indices = groupValues.withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> v == groupValue }.unzip().first
                result.add(data.entries.associate { (k, v) -> k to v.slice(indices) })
            }
            result
        } else {
            listOf(data)
        }
    }

    fun concat(datasets: List<Map<String, List<*>>>, emptyDataset: Map<String, List<*>>): Map<String, List<*>> {
        val keys = datasets.firstOrNull { data -> data.keys.any() }?.keys ?: return emptyDataset
        return keys.associateWith { key ->
            datasets.map { data -> data[key] ?: emptyList<Any?>() }
                .fold(emptyList<Any?>()) { result, values -> result + values }
        }
    }
}