package jetbrains.datalore.visualization.plot.gog.config.transform

object PlotSpecCleaner {

    /**
     * Makes muitable 'copy' and converts keys and values to canonic form.
     */
    fun apply(plotSpec: Map<*, *>): MutableMap<String, Any> {
        return cleanCopyOfMap(plotSpec)
    }

    private fun cleanCopyOfMap(map: Map<*, *>): MutableMap<String, Any> {
        // - drops key-value pair if value is null
        // - converts all keys to strings
        val out = HashMap<String, Any>()
        for (k in map.keys) {
            val v = map[k]
            if (v != null) {
                val key = k.toString()
                out[key] = cleanValue(v)
            }
        }
        return out
    }

    private fun cleanValue(v: Any): Any {
        if (v is Map<*, *>) {
            return cleanCopyOfMap(v)
        } else if (v is List<*>) {
            return cleanList(v)
        }
        return v
    }

    private fun cleanList(`in`: List<*>): List<*> {
        if (!containSpecs(`in`)) {
            // do not change data vectors
            return `in`
        }
        val copy = ArrayList<Any>(`in`.size)
        for (o in `in`) {
            copy.add(cleanValue(o!!))
        }
        return copy
    }

    private fun containSpecs(list: List<*>): Boolean {
        return list.stream().anyMatch { o -> o is Map<*, *> || o is List<*> }
    }
}
