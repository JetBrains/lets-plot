/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.Aes

class ConstantAes(
    aes: Aes<*>,
    private val label: String,
    format: String
) : MappedAes(aes) {

    private val myFormatter = if (format.isEmpty()) null else LineFormatter(format)

    override fun initLabel(): String {
        return LineFormatter.chooseLabel(dataLabel = getMappedDataLabel(), userLabel = label)
    }

    override fun getMappedDataPointValue(index: Int): String {
        val value = super.getMappedDataPointValue(index)
        return myFormatter?.format(value, myIsContinuous) ?: value
      }
}