/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.linetype

interface LineType {
    val isSolid: Boolean

    val isBlank: Boolean

    val dashArray: List<Double>
}
