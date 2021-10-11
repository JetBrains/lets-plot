/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Options<T>(
    val properties: MutableMap<String, Any?>
) {
    constructor() : this(mutableMapOf())

    inline fun <T, reified TValue> T.map(key: String): ReadWriteProperty<T, TValue?> {
        return object : ReadWriteProperty<T, TValue?> {
            override fun getValue(thisRef: T, property: KProperty<*>): TValue? = properties.get(key) as TValue?
            override fun setValue(thisRef: T, property: KProperty<*>, value: TValue?) = run { properties[key] = value }
        }
    }
}
