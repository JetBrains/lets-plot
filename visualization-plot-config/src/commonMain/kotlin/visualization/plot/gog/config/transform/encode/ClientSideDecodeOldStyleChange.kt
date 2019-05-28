package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChangeContext

/**
 * Feb 16, 2018
 * Included for backward compatibility to handle 'stored output' on opening old WB.
 * It's not fatal not to handle stored output but it will add exception stack-traces to logs.
 *
 *
 * This class can be removed after some reasonable period of time.
 */
internal class ClientSideDecodeOldStyleChange : SpecChange {

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return DataFrameEncoding.isEncodedDataFrame(spec)
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        //GWT.log("!!! Data in old format: " + String.valueOf(dataSpec));
        val dataFrame = DataFrameEncoding.decode(spec)

        spec.clear()
        spec.putAll(DataFrameUtil.toMap(dataFrame))
    }
}
