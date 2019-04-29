package jetbrains.datalore.visualization.plot.gog

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil

object FeatureSwitch {
    const val PLOT_DEBUG_DRAWING = false
    const val LEGEND_DEBUG_DRAWING = false

    const val PRINT_ENCODED_DATA_SUMMARY = false

    const val USE_DATA_FRAME_ENCODING = true

    fun printEncodedDataSummary(header: String, dataSpec: Map<String, Any>) {
        if (PRINT_ENCODED_DATA_SUMMARY) {
            printEncodedDataSummary(header, DataFrameUtil.fromMap(dataSpec))
        }
    }

    fun printEncodedDataSummary(header: String, df: DataFrame) {
        if (PRINT_ENCODED_DATA_SUMMARY) {
            //ToDo:
            //Preconditions.checkState(!GWT.isClient(), "Not expected on client")
            val summary = DataFrameUtil.getSummaryText(df)
            println(header)
            println(summary)
        }
    }
}
