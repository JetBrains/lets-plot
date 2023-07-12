/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.enums

object Enums {
    /**
     * Value of method for enums which takes into account toString() instead of saved generated name
     */
    inline fun <reified EnumT : Enum<EnumT>> valueOf(name: String): EnumT {
        for (e in enumValues<EnumT>()) {
            if (name == e.toString()) {
                return e
            }
        }

        throw IllegalArgumentException(name)
    }
}