/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.scale.transform.DateTimeBreaksGen

object ScaleProviderHelper {
    fun getOrCreateDefault(aes: Aes<*>, providers: Map<Aes<*>, ScaleProvider<*>>): ScaleProvider<*> {
        val realAes = when {
            Aes.isPositionalX(aes) -> Aes.X
            Aes.isPositionalY(aes) -> Aes.Y
            else -> aes
        }

        return providers[realAes] ?: createDefault(realAes)
    }

    fun <T> createDefault(aes: Aes<T>): ScaleProvider<T> {
        return ScaleProviderBuilder(aes).build()
    }

    fun <T> createDefault(aes: Aes<T>, name: String): ScaleProvider<T> {
        return ScaleProviderBuilder(aes)
            .name(name)
            .build()
    }

    fun <T> create(name: String, aes: Aes<T>, mapperProvider: MapperProvider<T>): ScaleProvider<T> {
        return ScaleProviderBuilder(aes)
            .mapperProvider(mapperProvider)
            .name(name)
            .build()
    }

    fun <T> createDateTimeScaleProvider(aes: Aes<T>, name: String): ScaleProvider<T> {
        return ScaleProviderBuilder(aes)
            .name(name)
            .breaksGenerator(DateTimeBreaksGen())
            .build()
    }
}
