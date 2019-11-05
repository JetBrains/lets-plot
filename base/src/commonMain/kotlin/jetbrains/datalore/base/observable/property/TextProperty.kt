/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

/**
 * implementations of [ReadableProperty.get] method shouldn't return null
 */
interface TextProperty : ReadableProperty<String> {
    /**
     * @throws IllegalArgumentException in the following cases:
     * {@param text} is null
     * {@param index} is negative
     * {@param index} is greater than property text length
     */
    fun insert(index: Int, text: String)

    /**
     * @throws IllegalArgumentException in the following cases:
     * {@param index} is negative
     * {@param length} is negative
     * {@param index} + {@param length} is greater than property text length
     */
    fun delete(index: Int, length: Int)
}