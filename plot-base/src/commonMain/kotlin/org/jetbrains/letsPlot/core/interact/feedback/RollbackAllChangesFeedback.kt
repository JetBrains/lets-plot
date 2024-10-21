/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.feedback

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.ToolFeedback
import org.jetbrains.letsPlot.core.interact.mouse.MouseDoubleClickInteraction

class RollbackAllChangesFeedback(
    private val onAction: ((targetId: String?) -> Unit)
) : ToolFeedback {

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDoubleClickInteraction(ctx)

        interaction.loop(
            onAction = {
                onAction(interaction.target.id)
            }
        )

        return object : Disposable {
            override fun dispose() {
                interaction.dispose()
            }
        }
    }
}