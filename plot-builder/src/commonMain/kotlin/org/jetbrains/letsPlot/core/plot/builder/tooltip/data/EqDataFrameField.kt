/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint
import kotlin.math.abs
import kotlin.math.sign

class EqDataFrameField(
    private val name: String,
    private val format: String? = null,
    private val lhs: String? = null,
    private val rhs: String?,
) : DataFrameField(name, format) {

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint? {

        val originalValue = myDataFrame[myVariable][index] ?: return null
        val coefficients = (originalValue as String).split(';').map { it.toDouble() }
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
        val formatter = myFormatter ?: initFormatter(ctx.expFormat, ctx.tz)
        val sb = StringBuilder()

        for (i in coefficients.lastIndex downTo 0) {

            if (coefficients[i] != 0.0) {

                if (!sb.isEmpty())
                    sb.append(if (sign(coefficients[i]) < 0 ) " - " else " + ")

                sb.append(formatter.invoke(abs(coefficients[i])))

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