/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.registration.Disposable

class RollbackAllChangesFeedback(
    private val onAction: (() -> Unit) = { println("RollbackAllChangesFeedback 'onAction'.") },
) : ToolFeedback {

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDoubleClickInteraction(ctx)

        interaction.loop(
            onAction = {
                println("RollbackAllChangesFeedback fire 'onAction'.")
                onAction()
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("RollbackAllChangesFeedback dispose.")
                interaction.dispose()
            }
        }
    }
}