/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipFormatting
import kotlin.math.abs
import kotlin.math.sign

class EqDataFrameField(
    private val name: String,
    private val format: String? = null,
    private val lhs: String? = null,
    private val rhs: String?,
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariables: List<DataFrame.Variable>
    private var myFormatters: List<(Any) -> String>? = null

    override val isSide: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        require(!::myDataAccess.isInitialized) { "Data context can be initialized only once" }
        myDataAccess = mappedDataAccess

        require(!::myDataFrame.isInitialized) { "Data context can be initialized only once" }
        myDataFrame = data
        myVariables = myDataFrame.variables().filter { it.label.contains("smooth_eq_coef_") }.sortedBy { it.label }
    }

    private fun initFormatters(expFormat: StringFormat.ExponentFormat, tz: TimeZone?): List<(Any) -> String> {
        require(myFormatters == null)

        myFormatters = myVariables.map {
            val f: (Any) -> String = when (format) {
                null -> TooltipFormatting.createFormatter(
                    it,
                    myDataAccess.defaultFormatters,
                    expFormat,
                    tz = tz
                )

                else -> FormatterUtil.byPattern(format, expFormat = expFormat, tz = tz)::format
            }

            f
        }

        return myFormatters!!
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint? {
        val coefficients = myVariables.map { myDataFrame.getNumeric(it)[index] ?: return null }
        val formattedValue = makeEq(coefficients, ctx)

        return DataPoint(
            label = name,
            value = formattedValue,
            aes = null,
            isAxis = false,
            isSide = false
        )
    }

    override fun copy(): EqDataFrameField {
        return EqDataFrameField(name, format, lhs, rhs)
    }

    fun makeEq(coefficients: List<Double>, ctx: PlotContext): String {
        val formatters = myFormatters ?: initFormatters(ctx.expFormat, ctx.tz)
        val sb = StringBuilder()

        for (i in coefficients.lastIndex downTo 0) {

            if (coefficients[i] != 0.0) {

                if (!sb.isEmpty())
                    sb.append(if (sign(coefficients[i]) < 0 ) " - " else " + ")

                sb.append(formatters[i].invoke(abs(coefficients[i])))

                if (i > 0)
                    sb.append(rhs ?: "x")

                if (i > 1)
                    sb.append("^").append(i)
            }
        }

        sb.insert(0, if (lhs != null) "$lhs=" else "")

        return "\\($sb\\)"
    }
}