/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.scale.ScaleUtil.labelByBreak
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureApplicableRange

internal class PointDataAccess(
    private val data: DataFrame,
    bindings: Map<Aes<*>, VarBinding>,
    scaleMap: TypedScaleMap
) : MappedDataAccess {

    private val scaleByAes: (Aes<*>) -> Scale<*> = { scaleMap[it] }
    private val myBindings: Map<Aes<*>, VarBinding> = bindings.toMap()
    private val myFormatters = HashMap<Aes<*>, (Any?) -> String>()

    override fun isMapped(aes: Aes<*>) = myBindings.containsKey(aes)

    override fun getOriginalValue(aes: Aes<*>, index: Int): Any? {
        require(isMapped(aes)) { "Not mapped: $aes" }

        val binding = myBindings.getValue(aes)
        val scale = getScale(aes)

        return binding.variable
            .let { variable -> data.getNumeric(variable)[index] }
            .let { value -> scale.transform.applyInverse(value) }
    }

    override fun getMappedDataValue(aes: Aes<*>, index: Int): String {
        val originalValue = getOriginalValue(aes, index)
        return formatter(aes).invoke(originalValue)
    }

    override fun getMappedDataLabel(aes: Aes<*>): String = getScale(aes).name

    private fun getScale(aes: Aes<*>): Scale<*> {
        return scaleByAes(aes)
    }

    private fun formatter(aes: Aes<*>): (Any?) -> String {
        val domainAes = when {
            Aes.isPositionalX(aes) && myBindings.containsKey(Aes.X) -> Aes.X
            Aes.isPositionalY(aes) && myBindings.containsKey(Aes.Y )-> Aes.Y
            else -> aes
        }

        val scale = getScale(domainAes)
        return myFormatters.getOrPut(domainAes, defaultValue = { createFormatter(domainAes, scale) })
    }

    private fun createFormatter(aes: Aes<*>, scale: Scale<*>): (Any?) -> String {
        if (scale.isContinuousDomain) {
            // only 'stat' or 'transform' vars here
            val domain = myBindings
                .getValue(aes)
                .variable
                .run(data::range)
                .run(::ensureApplicableRange)

            // Use the scale's default formatter (the 'format' parameter does not apply to tooltips)
            val formatter = scale.getBreaksGenerator().defaultFormatter(domain, 100)
            return { value -> value?.let { formatter.invoke(it) } ?: "n/a" }
        } else {
            val labelsMap = labelByBreak(scale)
            return { value -> value?.let { labelsMap[it] } ?: "n/a" }
        }
    }
}
