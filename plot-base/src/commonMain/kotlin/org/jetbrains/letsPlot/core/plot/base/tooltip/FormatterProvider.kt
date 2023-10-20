/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes

interface FormatterProvider {
    fun getFormatter(aes: Aes<*>): (Any?) -> String

    companion object {
        class EmptyFormatterProvider: FormatterProvider {
            override fun getFormatter(aes: Aes<*>): (Any?) -> String {
                return { "" }
            }
        }
    }
}