/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector

object PlotHtmlExport {
    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param version Version of Lets-plot JS library.
     * @param iFrame Whether to wrap HTML in IFrame
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildHtmlFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        version: String = "latest",
        iFrame: Boolean = false
    ): String {
        return buildHtmlFromRawSpecs(plotSpec, version, iFrame, null)
    }

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param version Version of Lets-plot JS library.
     * @param iFrame Whether to wrap HTML in IFrame
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildHtmlFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        version: String = "latest",
        iFrame: Boolean = false,
        plotSize: DoubleVector?
    ): String {

        val configureHtml = PlotHtmlHelper.getStaticConfigureHtml(PlotHtmlHelper.scriptUrl(version))
        val displayHtml = PlotHtmlHelper.getStaticDisplayHtmlForRawSpec(plotSpec, plotSize)

        val html = """
            |<html lang="en">
            |   <head>
            |       $configureHtml
            |   </head>
            |   <body>
            |       $displayHtml
            |   </body>
            |</html>
        """.trimMargin()

        return if (iFrame) {
            """
            <iframe srcdoc="${escapeHtmlAttr(html)}"></iframe>    
            """.trimIndent()
        } else {
            html
        }
    }

    private fun escapeHtmlAttr(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace("\"", "&quot;")
    }
}