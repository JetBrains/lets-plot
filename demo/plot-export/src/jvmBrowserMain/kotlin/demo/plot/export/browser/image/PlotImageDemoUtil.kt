/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.export.browser.image

import jetbrains.datalore.plot.PlotImageExport
import demo.common.util.demoUtils.browser.BrowserDemoUtil
import demo.common.util.demoUtils.browser.BrowserDemoUtil.createDemoFile
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.StringWriter

object PlotImageDemoUtil {
    private const val DEMO_PROJECT_PATH = "demo/plot-export"
    private const val DEMO_PROJECT = "demo-plot-export"

    fun show(
        title: String,
        plotSpec: MutableMap<String, Any>,
        scalingFactors: List<Double>,
        targetDPIs: List<Number>,
        formats: List<PlotImageExport.Format>
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
            getHtml(
                title,
                plotSpec,
                formats,
                scalingFactors,
                targetDPIs
            )
        }
    }

    private fun getHtml(
        title: String,
        plotSpec: MutableMap<String, Any>,
        formats: List<PlotImageExport.Format>,
        scaleFactors: List<Double>,
        targetDPIs: List<Number>
    ): String {
        require(formats.isNotEmpty()) { "Formats are not specified." }
        require(scaleFactors.size == formats.size) {
            "Parameters `scaleFactors` and `formats` must be a lists of equal sizes. Was: ${scaleFactors.size} and ${formats.size}."
        }

        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
                style {
                    unsafe {
                        +"""
                            div.demo {
                                border: 1px solid orange;
                                margin: 20px;
                                display: inline-block;
                            }
                            body { 
                                background-color:lightgrey
                            }
                        """.trimIndent()
                    }
                }
            }
            body {
                val zip = formats.zip(scaleFactors.zip(targetDPIs))
                zip.forEach { (format, scaleAndDPI) ->
                    val scalingFactor = scaleAndDPI.first
                    val targetDPI = scaleAndDPI.second
                    val image = PlotImageExport
                        .buildImageFromRawSpecs(
                            plotSpec = plotSpec,
                            format = format,
                            scalingFactor = scalingFactor,
                            targetDPI = targetDPI.toDouble()
                        )

                    val titleTrimmed = Regex("[^a-z0-9_]").replace(title.lowercase(), "_")
                    val namePrefix = "${titleTrimmed}_scale_${scalingFactor}_"
                    val imgFile = createDemoFile(DEMO_PROJECT_PATH, namePrefix, format.defFileExt)
                    imgFile.writeBytes(image.bytes)
                    val imgSrc = imgFile.toURI()

                    div("demo") {
                        p { +"${format} scaleFactor: $scalingFactor, DPI: ${targetDPI}" }
                        unsafe { +"<img src=\"$imgSrc\" width=\"${image.plotSize.x}\" height=\"${image.plotSize.y}\"/>" }
                    }
                }
            }
        }

        return writer.toString()
    }
}