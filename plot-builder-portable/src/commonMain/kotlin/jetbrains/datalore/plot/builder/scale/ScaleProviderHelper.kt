/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.scale.transform.DateTimeBreaksGen

object ScaleProviderHelper {
    fun <T> createDefault(aes: Aes<T>): ScaleProvider {
        return ScaleProviderBuilder(aes).build()
    }

    fun <T> createDateTimeScaleProvider(aes: Aes<T>, name: String): ScaleProvider {
        return ScaleProviderBuilder(aes)
            .name(name)
            .breaksGenerator(DateTimeBreaksGen())
            .build()
    }
}
