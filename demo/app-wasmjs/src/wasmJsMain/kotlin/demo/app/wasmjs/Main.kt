/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import kotlinx.browser.window

fun main() {
    when (DemoMode.fromSearch(window.location.search)) {
        DemoMode.SIMPLE_API -> SimpleApiDemo().start()
        DemoMode.LIVE_DEMO -> LiveDemo().start()
    }
}

private enum class DemoMode(val id: String) {
    SIMPLE_API("simple"),
    LIVE_DEMO("live");

    companion object {
        fun fromSearch(search: String): DemoMode {
            val demoId = search
                .removePrefix("?")
                .split("&")
                .firstOrNull { it.startsWith("demo=") }
                ?.substringAfter('=')

            return entries.firstOrNull { it.id == demoId } ?: SIMPLE_API
        }
    }
}

internal const val CONTROLS_ID = "controls"
internal const val DEMO_ROOT_ID = "demo-root"
internal const val STATUS_ID = "status"
