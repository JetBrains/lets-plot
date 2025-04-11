/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.geom

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.builder.VarBinding

internal class PointDataAccess(
    private val data: DataFrame,
    private val bindings: Map<Aes<*>, VarBinding>,
    private val scaleMap: Map<Aes<*>, Scale>,
    override val defaultFormatters: Map<Any, (Any) -> String>
) : MappedDataAccess {

    override fun isMapped(aes: Aes<*>) = bindings.containsKey(aes)

    override fun getOriginalValue(aes: Aes<*>, index: Int): Any? {
        require(isMapped(aes)) { "Not mapped: $aes" }

        val scale = scaleMap.getValue(aes)
        val transformedValue = getTransformedValue(aes, index)
        return scale.transform.applyInverse(transformedValue)
    }

    override fun getTransformedValue(aes: Aes<*>, index: Int): Double? {
        require(isMapped(aes)) { "Not mapped: $aes" }

        val binding = bindings.getValue(aes)
        return data.getNumeric(binding.variable)[index]
    }

    override fun getMappedDataLabel(aes: Aes<*>): String =
        scaleMap.getValue(aes).name
}
