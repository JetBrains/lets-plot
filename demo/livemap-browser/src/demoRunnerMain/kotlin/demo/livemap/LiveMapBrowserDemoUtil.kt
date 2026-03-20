/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap

import demo.common.utils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

object LiveMapBrowserDemoUtil {

    private const val DEMO_PROJECT_PATH = "demo/livemap-browser"

    fun openInBrowser(dev: Boolean? = null, html: () -> String) {
        BrowserDemoUtil.openInBrowserJs(DEMO_PROJECT_PATH, dev, html)
    }

    fun mapperDemoHtml(
        callFun: String,
        title: String,
        demoProject: String = "demo-livemap-browser",
        dev: Boolean? = null
    ): String {
        return BrowserDemoUtil.mapperDemoHtml(
            demoProjectPath = DEMO_PROJECT_PATH,
            demoProject = demoProject,
            callFun = callFun,
            title = title,
            dev = dev,
            projectDeps = emptyList(),
            target = BrowserDemoUtil.Target.JS
        )
    }
}
