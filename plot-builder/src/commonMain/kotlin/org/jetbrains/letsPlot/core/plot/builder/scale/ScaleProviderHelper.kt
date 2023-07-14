/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.core.plot.base.scale.transform.DateTimeBreaksGen

object ScaleProviderHelper {
    fun <T> createDefault(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): ScaleProvider {
        return ScaleProviderBuilder(aes).build()
    }

    fun <T> createDateTimeScaleProvider(
        aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>,
        name: String
    ): ScaleProvider {
        return ScaleProviderBuilder(aes)
            .name(name)
            .breaksGenerator(DateTimeBreaksGen())
            .build()
    }
}
