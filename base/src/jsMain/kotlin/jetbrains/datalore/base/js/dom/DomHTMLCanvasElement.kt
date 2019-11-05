/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

typealias DomHTMLCanvasElement = HTMLCanvasElement

val DomHTMLCanvasElement.context2d: DomContext2d
    get() = this.getContext("2d") as CanvasRenderingContext2D
