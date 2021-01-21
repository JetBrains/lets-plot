/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.enums.EnumInfoFactory

enum class TransformKind {
    IDENTITY,
    LOG10,
    REVERSE,
    SQRT;

    companion object {
        private val ENUM_INFO = EnumInfoFactory.createEnumInfo<TransformKind>()

        fun safeValueOf(name: String): TransformKind {
            return ENUM_INFO.safeValueOf(name) ?: throw IllegalArgumentException("Unknown transform name: '$name'")
        }
    }
}