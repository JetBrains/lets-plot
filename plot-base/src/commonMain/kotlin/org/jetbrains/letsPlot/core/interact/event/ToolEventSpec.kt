/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

object ToolEventSpec {
    // Event name
    const val INTERACTION_ACTIVATED = "interaction_activated"
    const val INTERACTION_DEACTIVATED = "interaction_deactivated"
    const val INTERACTION_UNSUPPORTED = "interaction_unsupported"
    const val SELECTION_CHANGED = "selection_changed"
    const val ROLLBACK_ALL_CHANGES = "rollback_all_changes"

    // properties
    const val EVENT_NAME = "name"
    const val EVENT_INTERACTION_ORIGIN = "origin"
    const val EVENT_INTERACTION_NAME = "interaction"
    const val EVENT_INTERACTION_TARGET = "target.id" // str
    const val EVENT_RESULT_DATA_BOUNDS = "selected.dataBounds" // 4-elements array of nullable numbers: [x,y,x1,y1]
    const val EVENT_RESULT_ERROR_MSG = "error.msg" // string
}