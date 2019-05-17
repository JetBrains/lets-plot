package jetbrains.datalore.base.domCore.dom

import jetbrains.datalore.base.domCore.css.StyleMap
import org.w3c.dom.HTMLElement

open class DomHTMLElement protected constructor(htmlElement: HTMLElement) : DomElement(htmlElement) {

    val htmlElement: HTMLElement
        get() = element as HTMLElement

    val style = StyleMap(htmlElement.style)

    fun getProperty(key: String): String {
        return style.getProperty(key)
    }

    var backgroundColor: String
        get() = style.backgroundColor
        set(value) {
            style.backgroundColor = value
        }

    var color: String
        get() = style.color
        set(value) {
            style.color = value
        }

    fun focus() {
        htmlElement.focus()
    }

    fun click() {
        htmlElement.click()
    }

    fun blur() {
        htmlElement.blur()
    }

    var title: String
        get() = htmlElement.title
        set(value) {
            htmlElement.title = value
        }

    val offsetParent: DomElement?
        get() = DomElement.create(htmlElement.offsetParent)

    val offsetWidth: Int
        get() = htmlElement.offsetWidth

    val offsetHeight: Int
        get() = htmlElement.offsetHeight

    val offsetLeft: Int
        get() = htmlElement.offsetLeft

    val offsetTop: Int
        get() = htmlElement.offsetTop

    companion object {
        fun create(htmlElement: HTMLElement?): DomHTMLElement? {
            //TODO don't create
            return if (htmlElement != null) DomHTMLElement(htmlElement) else null
        }
    }
}
