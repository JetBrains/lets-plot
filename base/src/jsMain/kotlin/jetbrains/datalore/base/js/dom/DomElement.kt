/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import org.w3c.dom.Element

typealias DomElement = Element

fun DomElement.hasClassName(className: String): Boolean {
    return classList.contains(className)
}

fun DomElement.removeClassName(className: String) {
    classList.remove(className)
}

fun DomElement.addClassName(className: String) {
    classList.add(className)
}

fun DomElement.addClassNames(vararg classNames: String) {
    classList.add(*classNames)
}

fun DomElement.replaceClassName(oldClassName: String, newClassName: String) {
    classList.remove(oldClassName)
    classList.add(newClassName)
}

val DomElement.childElements: DomElementList
    get() = this.children

val DomElement.childElementsList: List<DomElement>
    get() = DomList(this)

val DomElement.firstChildElement: DomElement?
    get() = this.firstElementChild

fun DomElement.setDisabled(state: Boolean) {
    if (state) {
        setAttribute("disabled", "disabled")
    } else {
        removeAttribute("disabled")
    }
}

fun DomElement.setTabIndex(index: Int) {
    setAttribute("tabindex", index.toString())
}

fun DomElement.clearTabIndex() {
    removeAttribute("tabindex")
}

fun DomElement.getAbsoluteLeft(): Double {
    return getBoundingClientRect().left
}

fun DomElement.getAbsoluteRight(): Double {
    return getBoundingClientRect().right
}

fun DomElement.getAbsoluteTop(): Double {
    return getBoundingClientRect().top
}

fun DomElement.getAbsoluteBottom(): Double {
    return getBoundingClientRect().bottom
}
