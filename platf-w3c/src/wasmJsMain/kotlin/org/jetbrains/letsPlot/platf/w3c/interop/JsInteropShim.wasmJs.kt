@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.interop

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.js.set


actual fun DoubleArray.toJsArray(): JsArray<JsNumber> {
    val jsArray = JsArray<JsNumber>()
    for (i in indices) {
        jsArray[i] = this[i].toJsNumber()
    }
    return jsArray
}

// Creates a native JS object that satisfies the EventListener interface
@JsFun("h => Object({ handleEvent: h })")
actual external fun createEventListener(h: (Event) -> Unit): EventListener


// Prevent false positive infinite recursion inspection

@JsFun("(arr, index) => arr[index]")
private external fun <T : JsAny?> getWasmArrayItem(arr: JsArray<T>, index: Int): T?
actual operator fun <T : JsAny?> JsArray<T>.get(index: Int): T? = getWasmArrayItem(this, index)

@JsFun("(arr, index, value) => { arr[index] = value; }")
private external fun <T : JsAny?> setWasmArrayItem(arr: JsArray<T>, index: Int, value: T)
actual operator fun <T : JsAny?> JsArray<T>.set(index: Int, value: T) = setWasmArrayItem(this, index, value)

@JsFun("(o) => Array.isArray(o)")
actual external fun isJsArray(o: JsAny): Boolean

@JsFun("(o) => Object.keys(o)")
actual external fun getJsObjectKeys(o: JsAny): JsArray<JsString>

@JsFun("(o, key) => o[key]")
actual external fun getJsProperty(o: JsAny, key: JsString): JsAny?