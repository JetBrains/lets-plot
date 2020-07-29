/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plot.PlotImageExport
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.createDemoFile
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.StringWriter

object PlotImageDemoUtil {
    private const val DEMO_PROJECT = "plot-demo"

    fun show(
        title: String,
        plotSpec: MutableMap<String, Any>,
        scaleFactors: List<Double>,
        formats: List<PlotImageExport.Format>
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
            getHtml(
                title,
                plotSpec,
                scaleFactors,
                formats
            )
        }
    }

    private fun getHtml(
        title: String,
        plotSpec: MutableMap<String, Any>,
        scaleFactors: List<Double>,
        formats: List<PlotImageExport.Format>
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
                formats.zip(scaleFactors).forEach { (format, scaleFactor) ->
                    val image = PlotImageExport
                        .buildImageFromRawSpecs(
                            plotSpec = plotSpec,
                            format = format,
                            scaleFactor = scaleFactor
                        )

                    val titleTrimmed = Regex("[^a-z0-9_]").replace(title.toLowerCase(), "_")
                    val namePrexix = "${titleTrimmed}_scale_${scaleFactor}_"
                    val imgFile = createDemoFile(DEMO_PROJECT, namePrexix, format.defFileExt)
                    imgFile.writeBytes(image.bytes)
                    val imgSrc = imgFile.toURI()

                    div("demo") {
                        p { +"scaleFactor: $scaleFactor, DPI: ${image.DPI} ${format.defFileExt}" }
                        unsafe { +"<img src=\"$imgSrc\" width=\"${image.plotSize.x}\" height=\"${image.plotSize.y}\"/>" }
                    }
                }
            }
        }

        return writer.toString()
    }
}