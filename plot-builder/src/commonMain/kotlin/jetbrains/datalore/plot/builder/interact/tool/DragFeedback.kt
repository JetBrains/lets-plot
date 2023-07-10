/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import org.jetbrains.letsPlot.commons.registration.Disposable

interface DragFeedback : ToolFeedback {
    fun start(ctx: InteractionContext): Disposable
}