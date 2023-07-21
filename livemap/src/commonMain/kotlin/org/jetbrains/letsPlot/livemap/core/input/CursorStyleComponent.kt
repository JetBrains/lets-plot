/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.input

import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent

class CursorStyleComponent(val cursorStyle: CursorStyle) : EcsComponent

enum class CursorStyle {
    POINTER
}