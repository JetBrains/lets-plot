/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector


/**
 *
 * We only need this "expect" in 'commonMain' root because of IDEA bug.
 * If we have just 'nativeMain' without "expect/actual",
 * IDEA (v 2023.1.3) fails to resolve reference on 'PlotSvgExportNative'
 * in another 'nativeMain' in 'python-extension' project.
 *
 * Note, Gradle (v 7.6.2) resolves such a 'native' -> 'native' dependencie without any issues.
 *
 */
expect object PlotSvgExportNative {
    /**
     * @param plotSpec Raw specification of a plot.
     * @param plotSize Desired plot size.
     * @param useCssPixelatedImageRendering true for CSS style "pixelated", false for SVG style "optimizeSpeed". Used for compatibility.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        useCssPixelatedImageRendering: Boolean = true
    ): String
}