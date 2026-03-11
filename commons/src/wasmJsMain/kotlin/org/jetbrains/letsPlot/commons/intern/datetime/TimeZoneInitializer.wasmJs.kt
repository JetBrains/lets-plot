package org.jetbrains.letsPlot.commons.intern.datetime


@OptIn(ExperimentalWasmJsInterop::class)
@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

actual object TimeZoneInitializer {
    actual fun initialize() {
    }
}