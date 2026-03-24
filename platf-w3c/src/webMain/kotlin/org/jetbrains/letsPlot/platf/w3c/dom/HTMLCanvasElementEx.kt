/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.dom

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.ExperimentalWasmJsInterop

val HTMLCanvasElement.context2d: CanvasRenderingContext2D
    get() = this.getContext("2d") as CanvasRenderingContext2D
