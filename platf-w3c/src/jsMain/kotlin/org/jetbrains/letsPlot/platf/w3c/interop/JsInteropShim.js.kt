@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.interop

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

actual fun DoubleArray.toJsArray(): JsArray<JsNumber> {
    val jsArray = JsArray<JsNumber>()
    for (i in indices) {
        jsArray[i] = this[i].toJsNumber()
    }
    return jsArray
}

actual fun createEventListener(h: (Event) -> Unit): EventListener = js("({ handleEvent: h })")

actual operator fun <T : JsAny?> JsArray<T>.get(index: Int): T? = asDynamic()[index] as T?

actual operator fun <T : JsAny?> JsArray<T>.set(index: Int, value: T) {
    asDynamic()[index] = value
}

actual fun isJsArray(o: JsAny): Boolean = js("Array.isArray(o)") as Boolean

actual fun getJsObjectKeys(o: JsAny): JsArray<JsString> = js("Object.keys(o)") as JsArray<JsString>

actual fun getJsProperty(o: JsAny, key: JsString): JsAny? = o.asDynamic()[key] as JsAny?