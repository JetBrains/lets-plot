/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

internal object PythonConfigStore {
    fun getPythonPath(): String? {
        val file = AppPaths.pythonConfigFile
        if (!file.exists()) return null
        return try {
            val map = JsonSupport.parseJson(file.readText()) as Map<*, *>
            map["pythonPath"] as? String
        } catch (_: Exception) {
            null
        }
    }

    fun setPythonPath(path: String) {
        val file = AppPaths.pythonConfigFile
        file.parentFile.mkdirs()
        file.writeText(JsonSupport.formatJson(mapOf("pythonPath" to path)))
    }
}
