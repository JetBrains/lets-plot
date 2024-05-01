/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

object ToolEventSpec {
    const val INTERACTION_ACTIVATED = "interaction_activated"
    const val INTERACTION_DEACTIVATED = "interaction_deactivated"

    // properties
    const val EVENT_NAME = "name"
    const val EVENT_INTERACTION_ORIGIN = "origin"
    const val EVENT_INTERACTION_NAME = "interaction"
}