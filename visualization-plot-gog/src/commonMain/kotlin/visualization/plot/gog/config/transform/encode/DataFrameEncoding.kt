package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.gog.common.base64.BinaryUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil

object DataFrameEncoding {
    internal val DATA_FRAME_KEY = "__data_frame_encoded" // depricated
    internal val DATA_SPEC_KEY = "__data_spec_encoded"

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
        //checkArgument(isDataFrame(map), "Not a data frame");
        checkArgument(isEncodedDataSpec(map), "Not an encoded data spec")

        val encodedData = map[DATA_SPEC_KEY] as List<*>

        val varNames = encodedData[0] as List<*>
        //List<?> varLabels = (List<?>) encodedData.get(1);
        val isNumeric = encodedData[1] as List<*>
        val seriesStart = 2

        //DataFrame.Builder b = new DataFrame.Builder();
        val decoded = HashMap<String, List<*>>()
        for (i in varNames.indices) {
            val name = varNames[i] as String
            //String label = (String) varLabels.get(i);
            val numeric = isNumeric[i] as Boolean

            //DataFrame.Variable variable = DataFrameUtil.createVariable(name, label);
            val o = encodedData[seriesStart + i]
            val v: List<*>
            if (numeric) {
                v = BinaryUtil.decodeList(o as String)
                //b.putNumeric(variable, v);
            } else {
                //b.put(variable, (List<?>) o);
                v = o as List<*>
            }
            decoded[name] = v
        }

        return decoded
    }
}
