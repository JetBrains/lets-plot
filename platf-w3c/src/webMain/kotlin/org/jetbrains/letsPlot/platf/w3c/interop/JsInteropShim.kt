@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.interop

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.js.*

expect fun DoubleArray.toJsArray(): JsArray<JsNumber>

/*
Do not rename "h" to "handler", otherwise it will cause build error (tested on Kotlin 2.3.10):
Task :wasmjs-package:wasmJsBrowserProductionWebpack FAILED
Module parse failed: Unexpected token (506:86)
*/
expect fun createEventListener(h: (Event) -> Unit): EventListener

expect operator fun <T : JsAny?> JsArray<T>.get(index: Int): T?

expect operator fun <T : JsAny?> JsArray<T>.set(index: Int, value: T)

expect fun isJsArray(o: JsAny): Boolean

expect fun getJsObjectKeys(o: JsAny): JsArray<JsString>

expect fun getJsProperty(o: JsAny, key: JsString): JsAny?
