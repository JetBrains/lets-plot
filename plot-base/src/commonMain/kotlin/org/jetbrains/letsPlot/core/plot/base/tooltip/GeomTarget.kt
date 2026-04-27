/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes

// `open` - for Mockito tests
open class GeomTarget(
    val hitIndex: Int,
    open val tooltipHint: TooltipHint,
    open val aesTooltipHint: Map<Aes<*>, TooltipHint>
)
