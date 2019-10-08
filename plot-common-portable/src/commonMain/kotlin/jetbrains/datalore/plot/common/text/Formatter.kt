package jetbrains.datalore.plot.common.text

import jetbrains.datalore.base.numberFormat.NumberFormat
import jetbrains.datalore.plot.common.data.DataType
import jetbrains.datalore.plot.common.time.interval.TimeInterval

object Formatter {
    private const val YEAR = "MMM y"
    private const val YEAR_QUARTER = "Q y"
    private const val YEAR_MONTH = "MMMM y"
    private const val DATE_MEDIUM = "EEE, MMM d, y"
    private const val DATE_MEDIUM_TIME_SHORT = "EEE, MMM d, y h:mm a"

    private val DEF_NUMBER_FORMAT = NumberFormat(",g")

    private val DEF_NUMBER_FORMATTER: (Any) -> String = { input ->
        DEF_NUMBER_FORMAT.apply(input as Number)
    }

    fun time(pattern: String): (Any) -> String {
        return { input ->
            DateTimeFormatUtil.formatDateUTC(
                input as Number,
                pattern
            )
        }
    }

//    @JvmOverloads
//    fun number(pattern: String): (Any) -> String = { input ->
//        var result = "NaN"
//        if (input is Number) {
//            result = NumberFormatUtil.formatNumber(input, pattern)
//        }
//        result
//    }

//    fun legend(dataType: DataType): (Any?) -> String {
//        return tooltip(dataType)
//    }

    // ToDo: remove - only used in tests (?)
    fun tooltip(dataType: DataType): (Any?) -> String {
        return nullable(
            tooltipImpl(
                dataType
            ), "null"
        )
    }

    private fun tooltipImpl(dataType: DataType): (Any) -> String {
        return when (dataType) {
            DataType.NUMBER -> DEF_NUMBER_FORMATTER
            DataType.STRING -> { it -> it.toString() } // no formatting really (toSting)
            DataType.INSTANT -> time(
                DATE_MEDIUM_TIME_SHORT
            )
            DataType.INSTANT_OF_DAY -> time(
                DATE_MEDIUM
            )
            DataType.INSTANT_OF_MONTH -> time(
                YEAR_MONTH
            )
            DataType.INSTANT_OF_QUARTER, DataType.INSTANT_OF_HALF_YEAR -> time(
                YEAR_QUARTER
            )
            DataType.INSTANT_OF_YEAR -> time(
                YEAR
            )
        }
    }

    // ToDo: remove - only used in tests (?)
    fun tableCell(dataType: DataType): (Any?) -> String {
        return tableCell(dataType, "null")
    }

    private fun tableCell(dataType: DataType, nullString: String): (Any?) -> String {
        return nullable(
            tableCellImpl(
                dataType
            ), nullString
        )
    }

    private fun tableCellImpl(dataType: DataType): (Any) -> String {
        when (dataType) {
            DataType.NUMBER -> return DEF_NUMBER_FORMATTER
            DataType.STRING -> return { it.toString() } // no formatting really (toSting)
            DataType.INSTANT -> return time(
                "EEE, MMM d, ''yy"
            )
            else -> if (dataType.isTimeInterval) {
                val timeInterval = TimeInterval.fromIntervalDataType(dataType)
                return timeInterval.tickFormatter
            }
        }

        throw IllegalArgumentException("Can't create formatter for data type $dataType")
    }

    private fun nullable(f: (Any) -> String, nullString: String): (Any?) -> String = { input ->
        if (input == null) nullString else f(input)
    }

//    fun ordinalSeries(dataType: DataType): (Any?) -> String {
//        return tableCell(dataType)
//    }
}
