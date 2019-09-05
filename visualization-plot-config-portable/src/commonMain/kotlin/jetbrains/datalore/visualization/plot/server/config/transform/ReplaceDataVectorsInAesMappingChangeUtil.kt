package jetbrains.datalore.visualization.plot.server.config.transform

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

    internal class AesMappingPreprocessor(private val myOpts: MutableMap<String, Any>, private val myDataKey: String, private val myMappingKey: String) {

        val dataColNames: List<String>
            get() {
                if (myOpts.containsKey(myDataKey)) {
                    val data = myOpts[myDataKey]
                    if (data is Map<*, *>) {
                        val dataMap = data as Map<String, *>
                        return ArrayList(dataMap.keys)
                    }
                }
                return emptyList()
            }

        fun replaceDataVectorsInAesMapping(usedVarNameCollector: MutableSet<String>) {
            if (!myOpts.containsKey(myMappingKey)) {
                return
            }

            val mappingValue = myOpts[myMappingKey] as? Map<*, *> ?: return

            val aesMapping = mappingValue as MutableMap<String, Any>
            val replacementAesMapping = HashMap<String, Any>()
            val addedDataVectors = HashMap<String, List<*>>()
            for (aesKey in aesMapping.keys) {
                val value = aesMapping[aesKey]
                if (value is List<*>) {
                    val varName = genVarName(aesKey, usedVarNameCollector)
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
                    val map = dataValue as MutableMap<String, Any>
                    data = map
                }
            }

            data.putAll(addedDataVectors)
            myOpts[myDataKey] = data
        }
    }
}
