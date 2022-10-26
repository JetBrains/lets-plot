/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Options(
    val properties: MutableMap<String, Any?> = mutableMapOf()
) {
    inline operator fun <reified T> get(aes: Aes<T>): T = properties[Option.Mapping.toOption(aes)] as T
    operator fun <T> set(aes: Aes<T>, v: T) { properties[Option.Mapping.toOption(aes)] = v }
}

inline fun <T: Options, reified TValue> map(key: String): ReadWriteProperty<T, TValue?> {
    return object : ReadWriteProperty<T, TValue?> {
        override fun getValue(thisRef: T, property: KProperty<*>): TValue? = thisRef.properties[key] as TValue?
        override fun setValue(thisRef: T, property: KProperty<*>, value: TValue?) = run { thisRef.properties[key] = value }
    }
}

inline fun <reified TValue> map(key: Aes<*>): ReadWriteProperty<Options, TValue?> = map(Option.Mapping.toOption(key))
