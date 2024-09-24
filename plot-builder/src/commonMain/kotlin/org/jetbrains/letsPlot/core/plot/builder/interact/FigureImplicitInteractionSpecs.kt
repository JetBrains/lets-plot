/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec

object FigureImplicitInteractionSpecs {
    val LIST = listOf(
        mapOf(
            ToolInteractionSpec.NAME to ToolInteractionSpec.ROLLBACK_ALL_CHANGES
        )
    )
}