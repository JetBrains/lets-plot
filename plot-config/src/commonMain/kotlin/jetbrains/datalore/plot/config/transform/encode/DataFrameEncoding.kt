package jetbrains.datalore.plot.config.transform.encode

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.common.base64.BinaryUtil
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil

object DataFrameEncoding {
    internal const val DATA_FRAME_KEY = "__data_frame_encoded" // depricated
    internal const val DATA_SPEC_KEY = "__data_spec_encoded"

    // deprecated
    fun isEncodedDataFrame(map: Map<*, *>): Boolean {
        return map.size == 1 && map.containsKey(DATA_FRAME_KEY)
    }

    fun isEncodedDataSpec(o: Any): Boolean {
        return if (o is Map<*, *>) {
            o.size == 1 && o.containsKey(DATA_SPEC_KEY)
        } else false
    }

    fun decode(map: Map<*, *>): DataFrame {
        checkArgument(isEncodedDataFrame(map), "Not a data frame")

        val encodedData = map[DATA_FRAME_KEY] as List<*>
        val varNames = encodedData[0] as List<*>
        val varLabels = encodedData[1] as List<*>
        val isNumeric = encodedData[2] as List<*>

        val b = DataFrame.Builder()
        for (i in varNames.indices) {
            val name = varNames[i] as String
            val label = varLabels[i] as String
            val numeric = isNumeric[i] as Boolean

            val variable = DataFrameUtil.createVariable(name, label)
            val o = encodedData[3 + i]
            if (numeric) {
                val v = BinaryUtil.decodeList(o as String)
                b.putNumeric(variable, v)
            } else {
                b.put(variable, o as List<*>)
            }
        }

        return b.build()
    }

    fun decode1(map: Map<String, *>): Map<String, List<*>> {
        checkArgument(isEncodedDataSpec(map), "Not an encoded data spec")

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

    fun encode(data: DataFrame): Map<String, *> {
        val map = HashMap<String, Any>()
        val encodedData = ArrayList<Any>()
        map[DATA_FRAME_KEY] = encodedData

        val varNames = ArrayList<String>()
        val varLabels = ArrayList<String>()
        val isNumeric = ArrayList<Boolean>()

        encodedData.add(varNames)
        encodedData.add(varLabels)
        encodedData.add(isNumeric)

        val variables = ArrayList(data.variables())
        // We need 'stable order' here.
        // If the order is not stable than encoded DataFrame wont pass 'equals' test and
        // plot will be re-built without a reason.
        variables.sortWith(Comparator { o1, o2 -> o1.name.compareTo(o2.name) })

        for (variable in variables) {
            varNames.add(variable.name)
            varLabels.add(variable.label)
            val numeric = data.isNumeric(variable)
            isNumeric.add(numeric)

            val v = data[variable]
            if (numeric) {
                // Safe cast: will fail in encoder
                val b64 = BinaryUtil.encodeList(v as List<Double>)
                encodedData.add(b64)
            } else {
                encodedData.add(v)
            }
        }

        return map
    }

    fun encode1(dataSpec: Map<String, Any>): Map<String, Any> {
        val encoded = HashMap<String, Any>()
        val encodedData = ArrayList<Any>()
        encoded[DATA_SPEC_KEY] = encodedData

        val varNames = ArrayList<String>()
        val isNumeric = ArrayList<Boolean>()

        encodedData.add(varNames)
        encodedData.add(isNumeric)

        val variables = ArrayList(dataSpec.keys)
        // We need 'stable order' here.
        // If the order is not stable than encoded DataFrame wont pass 'equals' test and
        // plot will be re-built without a reason.
        variables.sort()

        for (variable in variables) {
            val v = dataSpec[variable]
            if (v is List<*>) {
                val checkedDoubles = SeriesUtil.checkedDoubles(v)
                val numeric = checkedDoubles.notEmptyAndCanBeCast()

                varNames.add(variable)
                isNumeric.add(numeric)

                if (numeric) {   // Numeric vector?
                    val b64 = BinaryUtil.encodeList(checkedDoubles.cast())
                    encodedData.add(b64)
                } else {
                    encodedData.add(v)
                }
            }
        }

        return encoded
    }
}
