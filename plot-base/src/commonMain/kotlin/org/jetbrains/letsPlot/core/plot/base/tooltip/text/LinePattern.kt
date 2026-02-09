package org.jetbrains.letsPlot.core.plot.base.tooltip.text

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext

class LinePattern(
    private val label: String?,
    private val pattern: String,
    val fields: List<ValueSource>
) : LineSpec {

    constructor(other: LinePattern) : this(other.label, other.pattern, other.fields.map(ValueSource::copy))

    private var myLineFormatter: ((List<Any>) -> String)? = null

    private fun initFormatter(
        expFormat: StringFormat.ExponentFormat,
        tz: TimeZone?
    ): (List<Any>) -> String {
        require(myLineFormatter == null)

        // Do not use FormatterUtil.byPattern here - it will transform pattern-like ",.2f" to "{,.2f}"
        // But for the tooltip line it is unnecessary.
        myLineFormatter = StringFormat.of(pattern, expFormat = expFormat, tz = tz)::format
        return myLineFormatter!!
    }

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        fields.forEach { it.initDataContext(data, mappedDataAccess) }
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): LineSpec.DataPoint? {
        val formatter = myLineFormatter ?: initFormatter(ctx.expFormat, ctx.tz)

        val dataValues = fields.map { dataValue ->
            val p = dataValue.getDataPoint(index, ctx)
            if (p == null || p.isBlank) {
                // If the data point is blank, we return null to skip it.
                return null
            }

            p
        }
        return if (dataValues.size == 1) {
            val dataValue = dataValues.single()
            LineSpec.DataPoint(
                label = chooseLabel(dataValue.label),
                value = formatter.invoke(listOf(dataValue.value)),
                aes = dataValue.aes,
                isAxis = dataValue.isAxis,
                isSide = dataValue.isSide
            )
        } else {
            LineSpec.DataPoint(
                label = chooseLabel(dataValues.joinToString(", ") { it.label ?: "" }),
                value = formatter.invoke(dataValues.map(LineSpec.DataPoint::value)),
                aes = null,
                isAxis = false,
                isSide = false
            )
        }
    }

    private fun chooseLabel(dataLabel: String?): String? {
        return when (label) {
            DEFAULT_LABEL_SPECIFIER -> dataLabel    // use default label (from data)
            else -> label                     // use the given label (can be null)
        }
    }

    companion object {
        fun defaultLineForValueSource(valueSource: ValueSource): LinePattern = LinePattern(
            label = DEFAULT_LABEL_SPECIFIER,
            pattern = "{}",  // use original value
            fields = listOf(valueSource)
        )

        fun defaultLineForSmoothLabels(valueSource: ValueSource): LinePattern = LinePattern(
            label = DEFAULT_LABEL_SPECIFIER,
            pattern = "\\(R^2 = {}\\)",
            fields = listOf(valueSource)
        )

        private const val DEFAULT_LABEL_SPECIFIER = "@"


        fun prepareMappedLines(
            linePatterns: List<LinePattern>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame
        ): List<LinePattern> {
            val mappedLines = linePatterns.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingField>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedLines.forEach { it.initDataContext(dataFrame, dataAccess) }

            return mappedLines
        }
    }
}