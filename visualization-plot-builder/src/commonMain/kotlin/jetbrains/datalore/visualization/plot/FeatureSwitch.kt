package jetbrains.datalore.visualization.plot

import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil

object FeatureSwitch {
    const val PLOT_DEBUG_DRAWING = false
    const val LEGEND_DEBUG_DRAWING = false

    private const val PRINT_ENCODED_DATA_SUMMARY = false

    const val USE_DATA_FRAME_ENCODING = true

    fun printEncodedDataSummary(header: String, dataSpec: Map<String, Any>) {
        @Suppress("ConstantConditionIf")
        if (PRINT_ENCODED_DATA_SUMMARY) {
            printEncodedDataSummary(header, DataFrameUtil.fromMap(dataSpec))
        }
    }

    private fun printEncodedDataSummary(header: String, df: DataFrame) {
        @Suppress("ConstantConditionIf")
        if (PRINT_ENCODED_DATA_SUMMARY) {
            //ToDo:
            //Preconditions.checkState(!GWT.isClient(), "Not expected on client")
            val summary = DataFrameUtil.getSummaryText(df)
            println(header)
            println(summary)
        }
    }
}