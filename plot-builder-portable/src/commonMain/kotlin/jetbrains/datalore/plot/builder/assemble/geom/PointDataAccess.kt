/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.scale.ScaleUtil.labelByBreak
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap

internal class PointDataAccess(
    private val data: DataFrame,
    private val bindings: Map<Aes<*>, VarBinding>,
    private val scaleMap: TypedScaleMap,
    private val isYOrientation: Boolean
) : MappedDataAccess {

    private val myFormatters = HashMap<Aes<*>, (Any?) -> String>()

    override fun isMapped(aes: Aes<*>) = bindings.containsKey(aes)

    override fun getOriginalValue(aes: Aes<*>, index: Int): Any? {
        require(isMapped(aes)) { "Not mapped: $aes" }

        val binding = bindings.getValue(aes)
        val scale = scaleMap[aes]

        return binding.variable
            .let { variable -> data.getNumeric(variable)[index] }
            .let { value -> scale.transform.applyInverse(value) }
    }

    override fun getMappedDataValue(aes: Aes<*>, index: Int, ctx: PlotContext): String {
        val originalValue = getOriginalValue(aes, index)
        return formatter(aes, ctx).invoke(originalValue)
    }

    override fun getMappedDataLabel(aes: Aes<*>): String = scaleMap[aes].name

    private fun formatter(aes: Aes<*>, ctx: PlotContext): (Any?) -> String {
//        val scale = scaleMap[aes]
        return myFormatters.getOrPut(aes, defaultValue = { createFormatter(aes, ctx) })
    }

    private fun createFormatter(aes: Aes<*>, ctx: PlotContext): (Any?) -> String {
        // Positional aes need to be mapped on X or Y depending on the `orientation`.
        // Aes X and Y are ok - flipping is not needed.
        @Suppress("NAME_SHADOWING")
        val aes = when {
            aes == Aes.X || aes == Aes.Y -> aes
            Aes.isPositionalX(aes) -> if (isYOrientation) Aes.Y else Aes.X
            Aes.isPositionalY(aes) -> if (isYOrientation) Aes.X else Aes.Y
            else -> aes
        }
        val scale = ctx.getScale(aes)
        if (scale.isContinuousDomain) {
            // only 'stat' or 'transform' vars here
//            val domain = bindings
//                .getValue(aes)
//                .variable
//                .run(data::range)
//                .run(::ensureApplicableRange)

            val domain = ctx.overallTransformedDomain(aes)

            // Use the scale's default formatter (the 'format' parameter does not apply to tooltips)
            val formatter = scale.getBreaksGenerator().defaultFormatter(domain, 100)
            return { value -> value?.let { formatter.invoke(it) } ?: "n/a" }
        } else {
            val labelsMap = labelByBreak(scale)
            return { value -> value?.let { labelsMap[it] } ?: "n/a" }
        }
    }
}
