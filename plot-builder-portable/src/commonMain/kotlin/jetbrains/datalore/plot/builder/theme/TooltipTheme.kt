/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.plot.builder.guide.TooltipAnchor

interface TooltipTheme {

    fun isVisible(): Boolean

    fun anchor(): TooltipAnchor?
}