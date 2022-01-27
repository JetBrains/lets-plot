/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import jetbrains.datalore.vis.canvas.Context2d

interface RenderObject {
    fun render(ctx: Context2d)
}