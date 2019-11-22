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
import jetbrains.datalore.plot.base.scale.ScaleUtil.getBreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleUtil.labelByBreak
import jetbrains.datalore.plot.base.scale.transform.DateTimeBreaksGen
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureNotZeroRange

internal class PointDataAccess(
    private val data: DataFrame,
    bindings: Map<Aes<*>, VarBinding>
) : MappedDataAccess {

    override val mappedAes: Set<Aes<*>> = HashSet(bindings.keys)
    private val myBindings: Map<Aes<*>, VarBinding> = bindings.toMap()
    private val myFormatters = HashMap<Aes<*>, (Any?) -> String>()

    override fun isMapped(aes: Aes<*>) = myBindings.containsKey(aes)

    override fun <T> getMappedData(aes: Aes<T>, index: Int): MappedDataAccess.MappedData<T> {
        checkArgument(isMapped(aes), "Not mapped: $aes")

        val binding = myBindings.getValue(aes)
        val scale = binding.scale!!

        val originalValue = binding
            .variable
            .run(data::getNumeric)[index]
            .run(scale.transform::applyInverse)

        return MappedDataAccess.MappedData(
            label = scale.name,
            value = formatter(aes).invoke(originalValue),
            isContinuous = scale.isContinuous
        )
    }

    private fun <T> formatter(aes: Aes<T>): (Any?) -> String {
        val scale = myBindings.getValue(aes).scale
        return myFormatters.getOrPut(aes, defaultValue = { createFormatter(aes, scale!!) })
    }

    private fun createFormatter(aes: Aes<*>, scale: Scale<*>): (Any?) -> String {
        if (scale.isContinuousDomain) {
            // only 'stat' or 'transform' vars here
            val domain = myBindings
                .getValue(aes)
                .variable
                .run(data::range)
                .run(::ensureNotZeroRange)

            val breaksGenerator = getBreaksGenerator(scale)

            // hack: fix invalid labels for DateTime scale.
            // Large targetCount value for DateTime scale will break label format - instead of 'day'
            // format will be generated 'time' format and all points will have same label '24:00'.
            // Value of 10 still can fail (axis breaks count > 100, or data is very close to interval limit)
            // Proper fix - use the same targetCount value that was used for the axis breaks.
            val targetCount = 10.takeIf { breaksGenerator is DateTimeBreaksGen} ?: 100

            val formatter = breaksGenerator.labelFormatter(domain, targetCount)
            return { value -> value?.let { formatter.invoke(it) } ?: "NULL" }
        } else {
            val labelsMap = labelByBreak(scale)
            return { value -> value?.let { labelsMap.getValue(it) } ?: "NULL" }
        }
    }
}
