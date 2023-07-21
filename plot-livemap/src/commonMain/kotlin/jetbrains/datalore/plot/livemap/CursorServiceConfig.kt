/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.livemap.ui.CursorService

class CursorServiceConfig {
    val cursorService = CursorService()

    fun defaultSetter(default: () -> Unit) {
        cursorService.default = default
    }

    fun pointerSetter(pointer: () -> Unit) {
        cursorService.pointer = pointer
    }
}