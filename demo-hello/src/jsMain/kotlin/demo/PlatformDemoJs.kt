package demo

import kotlin.browser.document
import kotlin.browser.window

fun main() {
    val element = document.getElementById("demo-output")
    if (element != null) {
        element.textContent = PlatformClass().getName()
    } else {
        window.alert("Element `demo-output` not found")
    }
}