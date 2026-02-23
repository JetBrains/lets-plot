/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.text

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import kotlin.math.abs
import kotlin.math.sign

class EqDataFrameField(
    private val name: String,
    private val format: String? = null,
    private val eq: EqSpecification,
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

        myFormatters = myVariables.mapIndexed { i, variable ->
            val coefFormat = eq.formats.getOrNull(i)
                ?: eq.formats.lastOrNull()
                ?: format

            if (coefFormat != null) {
                FormatterUtil.byPattern(coefFormat, expFormat = expFormat, tz = tz)::format
            } else {
                TooltipFormatting.createFormatter(
                    variable,
                    myDataAccess.defaultFormatters,
                    expFormat,
                    tz = tz
                )
            }
        }

        return myFormatters!!
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): LineSpec.DataPoint? {
        val coefficients = myVariables.map { myDataFrame.getNumeric(it)[index] ?: return null }
        val formattedValue = makeEq(coefficients, ctx)

        return LineSpec.DataPoint(
            label = name,
            value = formattedValue,
            aes = null,
            isAxis = false,
            isSide = false
        )
    }

    override fun copy(): EqDataFrameField {
        return EqDataFrameField(name, format, eq)
    }

    private fun makeEq(coefficients: List<Double>, ctx: PlotContext): String {
        val sb = StringBuilder()

        val lhs = eq.lhs ?: "y"
        val formatters = (myFormatters ?: initFormatters(ctx.expFormat, ctx.tz))
            .reversed()     //Adjust the order of formatters to match the coefficients

        for (i in coefficients.lastIndex downTo 0) {
            var coef = coefficients[i]

            if (eq.threshold != null && abs(coef) < eq.threshold) {
                coef = 0.0
            }

            if (coef != 0.0) {

                if (!sb.isEmpty())
                    sb.append(if (sign(coef) < 0 ) " - " else " + ")

                sb.append(formatters[i].invoke(abs(coef)))

                if (i > 0) {
                    sb.append("\\(")
                    sb.append(eq.rhs ?: "x")

                    if (i > 1) {
                        sb.append("^").append(i)
                    }
                    sb.append("\\)")
                }
            }
        }

        sb.insert(0, if (lhs != "") "\\($lhs=\\)" else "")

        return "$sb"
    }
}