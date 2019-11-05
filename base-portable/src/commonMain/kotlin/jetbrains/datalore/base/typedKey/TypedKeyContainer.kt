/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedKey

interface TypedKeyContainer {
    operator fun <T> get(key: TypedKey<T>): T
    fun <T> put(key: TypedKey<T>, value: T): T
    operator fun contains(key: TypedKey<*>): Boolean
}
