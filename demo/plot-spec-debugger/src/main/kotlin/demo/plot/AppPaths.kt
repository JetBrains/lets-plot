/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.io.File

internal object AppPaths {
    private const val APP_DIR_NAME = ".lets-plot-debugger"

    private val appDir: File
        get() = File(System.getProperty("user.home"), APP_DIR_NAME)

    val lastSpecFile: File get() = File(appDir, "last_spec.json")
    val favoritesFile: File get() = File(appDir, "favorites.json")
    val tempFile: File get() = File(appDir, "temp.json")
    val pythonConfigFile: File get() = File(appDir, "python_config.json")
    val previewsDir: File get() = File(appDir, "previews")
}
