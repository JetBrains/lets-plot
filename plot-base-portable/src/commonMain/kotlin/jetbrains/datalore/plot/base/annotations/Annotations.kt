/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.annotations

import jetbrains.datalore.vis.TextStyle

class Annotations(
    private val lines: List<AnnotationLineSpec>,
    val textStyle: TextStyle
) {
    fun getAnnotationText(index: Int): String {
         return lines.mapNotNull { it.getAnnotationText(index) }.joinToString("\n")
    }
}