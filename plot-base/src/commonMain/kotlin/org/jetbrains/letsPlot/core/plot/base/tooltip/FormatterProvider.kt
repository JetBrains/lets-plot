/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame

interface FormatterProvider {
    fun getFormatter(aes: Aes<*>): (Any?) -> String
    fun getFormatter(variable: DataFrame.Variable): (Any) -> String

    companion object {
        val DUMMY = object : FormatterProvider {
            override fun getFormatter(aes: Aes<*>): (Any?) -> String {
                UNSUPPORTED("Dummy FormatterProvider")
            }
            override fun getFormatter(variable: DataFrame.Variable): (Any) -> String {
                UNSUPPORTED("Dummy FormatterProvider")
            }
        }
    }
}