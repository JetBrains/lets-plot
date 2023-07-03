/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.js.dom

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

val HTMLCanvasElement.context2d: CanvasRenderingContext2D
    get() = this.getContext("2d") as CanvasRenderingContext2D
