package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

class DomHTMLCanvasElement protected constructor(htmlCanvasElement: HTMLCanvasElement) : DomHTMLElement(htmlCanvasElement) {
    val htmlCanvasElement: HTMLCanvasElement
        get() = htmlElement as HTMLCanvasElement

    val context2d: DomContext2d
        get() = getContext("2d")!!

    fun getContext(context: String): DomContext2d? {
        return htmlCanvasElement.getContext(context) as CanvasRenderingContext2D?
    }

    fun toDataURL(type: String): String {
        return htmlCanvasElement.toDataURL(type)
    }

    var width: Int
        get() = htmlCanvasElement.width
        set(value) {
            htmlCanvasElement.width = value
        }

    var height: Int
        get() = htmlCanvasElement.height
        set(value) {
            htmlCanvasElement.height = value
        }
}
