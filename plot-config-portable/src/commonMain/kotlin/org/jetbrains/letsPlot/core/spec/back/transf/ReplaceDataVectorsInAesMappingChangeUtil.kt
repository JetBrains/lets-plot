/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf

import org.jetbrains.letsPlot.core.spec.back.transf.NumericDataVectorChangeUtil.containsNumbersToConvert
import org.jetbrains.letsPlot.core.spec.back.transf.NumericDataVectorChangeUtil.convertNumbersToDouble

internal object ReplaceDataVectorsInAesMappingChangeUtil {

    private fun genVarName(baseName: String, usedNamesCollector: MutableSet<String>): String {
        var name = baseName
        var counter = 1
        while (usedNamesCollector.contains(name)) {
            name = baseName + counter++
        }

        usedNamesCollector.add(name)
        return name
    }

    internal class AesMappingPreprocessor(
        private val myOpts: MutableMap<String, Any>,
        private val myDataKey: String,
        private val myMappingKey: String
    ) {

        val dataColNames: List<String>
            get() {
                if (myOpts.containsKey(myDataKey)) {
                    val data = myOpts[myDataKey]
                    if (data is Map<*, *>) {
                        @Suppress("UNCHECKED_CAST")
                        return ArrayList(data.keys as Set<String>)
                    }
                }
                return emptyList()
            }

        fun replaceDataVectorsInAesMapping(usedVarNameCollector: MutableSet<String>) {
            if (!myOpts.containsKey(myMappingKey)) {
                return
            }

            @Suppress("UNCHECKED_CAST")
            val aesMapping = myOpts[myMappingKey] as? MutableMap<String, Any> ?: return

            val replacementAesMapping = HashMap<String, Any>()
            val addedDataVectors = HashMap<String, List<*>>()
            for (aesKey in aesMapping.keys) {
                val value = aesMapping[aesKey]
                if (value is List<*>) {
                    val varName = genVarName(
                        aesKey,
                        usedVarNameCollector
                    )
                    replacementAesMapping[aesKey] = varName
                    addedDataVectors[varName] = value
                }
            }

            if (!addedDataVectors.isEmpty()) {
                aesMapping.putAll(replacementAesMapping)
                addedDataVectors(addedDataVectors)
            }
        }

        private fun addedDataVectors(addedDataVectors: Map<String, List<*>>) {
            var data: MutableMap<String, Any> = HashMap()
            if (myOpts.containsKey(myDataKey)) {
                val dataValue = myOpts[myDataKey]
                if (dataValue is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    data = dataValue as MutableMap<String, Any>
                }
            }

            val processedVectors = addedDataVectors.mapValues { (_, list) ->
                if (containsNumbersToConvert(list)) {
                    convertNumbersToDouble(list)
                } else {
                    list
                }
            }

            data.putAll(processedVectors)
            myOpts[myDataKey] = data
        }
    }
}
