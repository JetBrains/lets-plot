package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.HTMLInputElement

typealias InputDomElement = HTMLInputElement

fun InputDomElement.isChecked(): Boolean {
    return checked
}
