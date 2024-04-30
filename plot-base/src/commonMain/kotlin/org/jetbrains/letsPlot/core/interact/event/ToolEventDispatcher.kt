/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

interface ToolEventDispatcher {
    fun activateInteraction(origin: String, interactionSpec: Map<String, Any>): Map<String, Any>
    fun deactivateInteraction(origin: String, interactionName: String): Map<String, Any>
}