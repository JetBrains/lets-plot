/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.enums

/**
 * 1. enumConstant.toString() is used instead of enumConstant.name().
 * 2. enum constant names are case insenstive.
 */
interface EnumInfo<EnumT : Enum<EnumT>> {

    val originalNames: List<String>

    fun hasValue(name: String?): Boolean

    /**
     * Similar to valueOf(). See the notes for this interface.
     */
    fun unsafeValueOf(name: String): EnumT

    fun safeValueOf(name: String?): EnumT?

    fun safeValueOf(name: String?, defaultValue: EnumT): EnumT

}
