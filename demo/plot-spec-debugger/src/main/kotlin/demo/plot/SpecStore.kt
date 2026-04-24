/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.io.IOException

internal object SpecStore {
    fun load(): String? {
        val file = AppPaths.lastSpecFile
        return try {
            if (file.isFile && file.canRead()) {
                val content = file.readText()
                if (content.isNotBlank()) content else null
            } else {
                null
            }
        } catch (e: IOException) {
            println("Warning: Could not read previous plot spec from ${file.absolutePath}")
            e.printStackTrace()
            null
        }
    }

    fun save(spec: String) {
        val file = AppPaths.lastSpecFile
        try {
            file.parentFile.mkdirs()
            file.writeText(spec)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
