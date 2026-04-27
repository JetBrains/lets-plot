/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core

/*
Skip all (Including visual) tests in module:
-x ${MODULE_NAME}:check

To build python wheel:

./gradlew :python-package-build:buildPythonPackage \
  -x :python-extension:check

 */
object FeatureSwitch {

    const val PLOT_VIEW_TOOLBOX_HTML = false

    // When 'debug drawing' is enabled, you need to exclude visual tests from the build:
    // ./gradlew build -x test -x macosArm64Test
    const val PLOT_DEBUG_DRAWING = false
    const val LEGEND_DEBUG_DRAWING = false
}