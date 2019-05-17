package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.Element

open class DomElement protected constructor(element: Element) : DomNode(element) {

    private var myValidationError: String? = null

    val element: Element
        get() = node as Element

    val classList: DomTokenList
        get() = element.classList

    fun hasClassName(className: String): Boolean {
        return classList.contains(className)
    }

    fun removeClassName(className: String) {
        classList.remove(className)
    }

    fun addClassName(className: String) {
        classList.add(className)
    }

    fun addClassNames(vararg classNames: String) {
        classList.add(*classNames)
    }

    fun replaceClassName(oldClassName: String, newClassName: String) {
        classList.remove(oldClassName)
        classList.add(newClassName)
    }

    val tagName: String
        get() = element.tagName

    var innerHTML: String
        get() = element.innerHTML
        set(value) {
            element.innerHTML = value
        }

    var outerHTML: String
        get() = element.outerHTML
        set(value) {
            element.outerHTML = value
        }

    var scrollLeft: Double
        get() = element.scrollLeft
        set(value) {
            element.scrollLeft = value
        }

    var scrollTop: Double
        get() = element.scrollTop
        set(value) {
            element.scrollTop = value
        }

    val scrollWidth: Int
        get() = element.scrollWidth

    val scrollHeight: Int
        get() = element.scrollHeight

    val clientWidth: Int
        get() = element.clientWidth

    val clientHeight: Int
        get() = element.clientHeight

    val childElements: DomElementList
        get() = element.children

    val childElementsList: List<DomElement>
        get() = DomList(this)

    val firstChildElement: DomElement?
        get() = create(element.firstElementChild)

    fun scrollIntoView() {
        element.scrollIntoView()
    }

    fun getAttribute(key: String): String? {
        return element.getAttribute(key)
    }

    fun hasAttribute(key: String): Boolean {
        return element.hasAttribute(key)
    }

    fun setAttribute(key: String, value: String) {
        return element.setAttribute(key, value)
    }

    fun removeAttribute(key: String) {
        return element.removeAttribute(key)
    }

    fun setAttributeNS(namespace: String?, name: String, value: String) {
        return element.setAttributeNS(namespace, name, value)
    }

    var id: String
        get() = element.id
        set(value) {
            element.id = value
        }

    fun setDisabled(state: Boolean) {
        if (state) {
            setAttribute("disabled", "disabled")
        } else {
            removeAttribute("disabled")
        }
    }

    fun setTabIndex(index: Int) {
        setAttribute("tabindex", index.toString())
    }

    fun clearTabIndex() {
        removeAttribute("tabindex")
    }

    fun getBoundingClientRect(): DomRect {
        return element.getBoundingClientRect()
    }

    fun getAbsoluteLeft(): Double {
        return getBoundingClientRect().left
    }

    fun getAbsoluteRight(): Double {
        return getBoundingClientRect().right
    }

    fun getAbsoluteTop(): Double {
        return getBoundingClientRect().top
    }

    fun getAbsoluteBottom(): Double {
        return getBoundingClientRect().bottom
    }

    fun querySelector(selector: String): DomElement? {
        return create(element.querySelector(selector))
    }

    fun querySelectorAll(selectors: String): DomNodeList {
        return element.querySelectorAll(selectors)
    }

    fun closest(selector: String): DomElement? {
        return create(element.closest(selector))
    }

    fun getValidationMessage(): String? {
        return myValidationError
    }

    fun setCustomValidity(message: String?) {
        myValidationError = message
        if (message != null && "" != message) {
            addClassName("error")
        } else {
            removeClassName("error")
        }
    }

    companion object {
        fun create(element: Element?): DomElement? {
            //TODO don't create
            return if (element != null) DomElement(element) else null
        }
    }
}
