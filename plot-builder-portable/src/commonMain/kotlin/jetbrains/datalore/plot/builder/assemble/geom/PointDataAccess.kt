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

    override val mappedAes: Set<Aes<*>> = HashSet(bindings.keys)
    override val scaleByAes: (Aes<*>) -> Scale<*> = { scaleMap[it] }
    private val myBindings: Map<Aes<*>, VarBinding> = bindings.toMap()
    private val myFormatters = HashMap<Aes<*>, (Any?) -> String>()

    override fun isMapped(aes: Aes<*>) = myBindings.containsKey(aes)

    override fun <T> getMappedData(aes: Aes<T>, index: Int): MappedDataAccess.MappedData<T> {
        val originalValue = getOriginalValue(aes, index)
        val scale = getScale(aes)
        val value = formatter(aes).invoke(originalValue)
        return MappedDataAccess.MappedData(
            label = scale.name,
            value = value,
            isContinuous = scale.isContinuous
        )
    }

    override fun <T> getOriginalValue(aes: Aes<T>, index: Int): Any? {
        require(isMapped(aes)) { "Not mapped: $aes" }

        val binding = myBindings.getValue(aes)
        val scale = getScale(aes)

        return binding.variable
            .let { variable -> data.getNumeric(variable)[index] }
            .let { value -> scale.transform.applyInverse(value) }
    }

    override fun getMappedDataLabel(aes: Aes<*>): String = getScale(aes).name

    override fun isMappedDataContinuous(aes: Aes<*>): Boolean = getScale(aes).isContinuous

    private fun getScale(aes: Aes<*>): Scale<*> {
        return scaleByAes(aes)
    }

    private fun <T> formatter(aes: Aes<T>): (Any?) -> String {
        val scale = getScale(aes)
        return myFormatters.getOrPut(aes, defaultValue = { createFormatter(aes, scale) })
    }

    private fun createFormatter(aes: Aes<*>, scale: Scale<*>): (Any?) -> String {
        if (scale.isContinuousDomain) {
            // only 'stat' or 'transform' vars here
            val domain = myBindings
                .getValue(aes)
                .variable
                .run(data::range)
                .run(::ensureApplicableRange)

            val formatter = scale.getBreaksGenerator().labelFormatter(domain, 100)
            return { value -> value?.let { formatter.invoke(it) } ?: "n/a" }
        } else {
            val labelsMap = labelByBreak(scale)
            return { value -> value?.let { labelsMap.getValue(it) } ?: "n/a" }
        }
    }
}
