package jetbrains.datalore.base.js.dom

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

typealias DomHTMLCanvasElement = HTMLCanvasElement

val DomHTMLCanvasElement.context2d: DomContext2d
    get() = this.getContext("2d") as CanvasRenderingContext2D
