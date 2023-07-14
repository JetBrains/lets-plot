/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.enums

object EnumInfoFactory {

    /**
     * @throws IllegalArgumentException if there are same enumConstant.toString() values (case insensitive) in the enum
     */
    inline fun <reified EnumT : Enum<EnumT>> createEnumInfo(): EnumInfo<EnumT> {
        return EnumInfoImpl(enumValues())
    }
}
