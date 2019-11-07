/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil

object FeatureSwitch {
    const val PLOT_DEBUG_DRAWING = false
    const val LEGEND_DEBUG_DRAWING = false
    private const val PRINT_DEBUG_LOGS = false

    private const val PRINT_ENCODED_DATA_SUMMARY = false

    const val USE_DATA_FRAME_ENCODING = true

    fun printEncodedDataSummary(header: String, dataSpec: Map<String, Any>) {
        @Suppress("ConstantConditionIf")
        if (PRINT_ENCODED_DATA_SUMMARY) {
            printEncodedDataSummary(header, DataFrameUtil.fromMap(dataSpec))
        }
    }

    fun isDebugLogEnabled(): Boolean {
        return PRINT_DEBUG_LOGS
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