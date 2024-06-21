/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.scale.transform.DateTimeBreaksGen

object ScaleProviderHelper {
    fun <T> createDefault(aes: Aes<T>): ScaleProvider {
        return ScaleProviderBuilder(aes).build()
    }

    fun <T> createDateTimeScaleProviderBuilder(aes: Aes<T>): ScaleProviderBuilder<T> {
        return ScaleProviderBuilder(aes)
            .breaksGenerator(DateTimeBreaksGen())
    }
}
