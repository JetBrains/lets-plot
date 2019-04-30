package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.visualization.plot.gog.common.base64.BinaryUtil
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame

fun DataFrameEncoding.encode(data: DataFrame): Map<String, *> {
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

fun DataFrameEncoding.encode1(dataSpec: Map<String, Any>): Map<String, Any> {
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
