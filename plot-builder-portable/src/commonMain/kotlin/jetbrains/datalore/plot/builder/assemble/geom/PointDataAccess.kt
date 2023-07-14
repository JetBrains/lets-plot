/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import jetbrains.datalore.plot.builder.VarBinding

internal class PointDataAccess(
    private val data: DataFrame,
    private val bindings: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, VarBinding>,
    private val scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>,
    override val isYOrientation: Boolean
) : MappedDataAccess {

    private val myFormatters = HashMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, (Any?) -> String>()

    override fun isMapped(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>) = bindings.containsKey(aes)

    override fun getOriginalValue(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>, index: Int): Any? {
        require(isMapped(aes)) { "Not mapped: $aes" }

        val binding = bindings.getValue(aes)
        val scale = scaleMap.getValue(aes)

        return binding.variable
            .let { variable -> data.getNumeric(variable)[index] }
            .let { value -> scale.transform.applyInverse(value) }
    }

    override fun getMappedDataLabel(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): String = scaleMap.getValue(aes).name
}
