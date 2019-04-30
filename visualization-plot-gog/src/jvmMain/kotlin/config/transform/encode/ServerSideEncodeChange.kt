package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.visualization.plot.gog.FeatureSwitch
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChangeContext

internal class ServerSideEncodeChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        FeatureSwitch.printEncodedDataSummary("DataFrameOptionHelper.encodeUpdateOption", spec)

        if (FeatureSwitch.USE_DATA_FRAME_ENCODING) {
            val encoded = DataFrameEncoding.encode1(spec)
            spec.clear()
            spec.putAll(encoded)
        }
    }
}
