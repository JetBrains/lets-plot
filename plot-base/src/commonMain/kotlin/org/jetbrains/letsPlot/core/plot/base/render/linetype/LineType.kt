/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.linetype

interface LineType {
    val isSolid: Boolean

    val isBlank: Boolean

    val dashArray: List<Double>
}
