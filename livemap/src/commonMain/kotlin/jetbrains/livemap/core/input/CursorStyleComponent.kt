/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.livemap.core.ecs.EcsComponent

class CursorStyleComponent(val cursorStyle: CursorStyle) : EcsComponent

enum class CursorStyle {
    POINTER
}