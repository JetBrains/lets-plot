/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.scale.breaks.QuantitativeTickFormatterFactory
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class PointDataAccess(
    private val data: DataFrame,
    bindings: Map<Aes<*>, VarBinding>
) : MappedDataAccess {

    override val mappedAes: Set<Aes<*>> = HashSet(bindings.keys)

    private val myBindings: Map<Aes<*>, VarBinding> = bindings.toMap()

    private val myFormatters = HashMap<Aes<*>, (Any) -> String>()

    override fun isMapped(aes: Aes<*>): Boolean {
        return myBindings.containsKey(aes)
    }

    override fun <T> getMappedData(aes: Aes<T>, index: Int): MappedDataAccess.MappedData<T> {
        checkArgument(isMapped(aes), "Not mapped: $aes")

        val value = valueAfterTransform(aes, index)!!
        @Suppress("UNCHECKED_CAST")
        val scale = myBindings[aes]!!.scale as Scale<T>

        val original = scale.transform.applyInverse(value)
        val s = when (original) {
            is Number -> formatter(aes, scale)(original)
            else -> original.toString()
        }

        val continuous = scale.isContinuous

        return MappedDataAccess.MappedData(label(aes), s, continuous)
    }

    private fun label(aes: Aes<*>): String {
        return myBindings[aes]!!.scale!!.name
    }

    private fun valueAfterTransform(aes: Aes<*>, index: Int): Double? {
        val variable = myBindings[aes]!!.variable
        return data.getNumeric(variable)[index]
    }

    private fun formatter(
        aes: Aes<*>,
        scale: Scale<*>
    ): (Any) -> String {
        if (!myFormatters.containsKey(aes)) {
            myFormatters[aes] = createFormatter(aes, scale)
        }
        return myFormatters[aes]!!
    }

    private fun createFormatter(
        aes: Aes<*>,
        scale: Scale<*>
    ): (Any) -> String {

        val binding = myBindings.getValue(aes)
        // only 'stat' or 'transform' vars here
        val domain = SeriesUtil.ensureNotZeroRange(data.range(binding.variable))
        if (scale.hasBreaksGenerator()) {
            // targetCount should have pretty low value. data.rowCount() can give e-notation where it is not needed
            return scale.breaksGenerator.labelFormatter(domain, 100)
        }

        return QuantitativeTickFormatterFactory.forLinearScale().getFormatter(domain, SeriesUtil.span(domain) / 100.0)
    }
}
