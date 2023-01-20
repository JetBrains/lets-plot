/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

internal data class AxisLayoutQuad(
    val left: AxisLayout?,
    val right: AxisLayout?,
    val top: AxisLayout?,
    val bottom: AxisLayout?,
)
