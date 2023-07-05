/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

/**
 * An object which allows writing to a value stored somewhere
 */
interface WritableProperty<ValueT> {
    fun set(value: ValueT)
}