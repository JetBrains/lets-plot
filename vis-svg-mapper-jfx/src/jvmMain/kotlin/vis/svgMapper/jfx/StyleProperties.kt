/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import jetbrains.datalore.base.values.Color

interface StyleProperties {
    fun getColor(className: String): Color
    fun getFontSize(className: String): Double
    fun getFontFamily(className: String): String
    fun getIsItalic(className: String): Boolean
    fun getIsBold(className: String): Boolean
}