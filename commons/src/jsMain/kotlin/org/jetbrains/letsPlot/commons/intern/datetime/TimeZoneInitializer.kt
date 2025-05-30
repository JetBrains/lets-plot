/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

@JsModule("@js-joda/timezone")
@JsNonModule
private external object JsJodaTimeZoneModule

// This private val ensures the module is referenced and not eliminated by DCE
private val jsJodaTz = JsJodaTimeZoneModule

actual object TimeZoneInitializer {
    actual fun initialize() {
        // The extra protection from DCE (maybe not needed)
        js("/* @preserve */ {};")
        // Referencing the val explicitly in the function body
        jsJodaTz
    }
}