package jetbrains.datalore.visualization.plot.config.transform.encode

import jetbrains.datalore.visualization.plot.FeatureSwitch
import jetbrains.datalore.visualization.plot.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.config.transform.SpecChangeContext

internal class ServerSideEncodeChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        FeatureSwitch.printEncodedDataSummary("DataFrameOptionHelper.encodeUpdateOption", spec)

        @Suppress("ConstantConditionIf")
        if (FeatureSwitch.USE_DATA_FRAME_ENCODING) {
            val encoded = DataFrameEncoding.encode1(spec)
            spec.clear()
            spec.putAll(encoded)
        }
    }
}
