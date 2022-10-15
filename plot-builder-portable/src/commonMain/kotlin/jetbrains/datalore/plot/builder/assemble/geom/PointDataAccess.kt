/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap

internal class PointDataAccess(
    private val data: DataFrame,
    private val bindings: Map<Aes<*>, VarBinding>,
    private val scaleMap: TypedScaleMap,
    override val isYOrientation: Boolean
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

    override fun getMappedDataLabel(aes: Aes<*>): String = scaleMap[aes].name
}
