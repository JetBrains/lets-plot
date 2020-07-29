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
    private val FORMAT = PlotImageExport.Format.PNG

    fun show(
        title: String,
        plotSpec: MutableMap<String, Any>,
        scaleFactors: List<Double>
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
            getHtml(
                title,
                MutableList(scaleFactors.size) { plotSpec },
                scaleFactors
            )
        }
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        scaleFactors: List<Double>
    ): String {
        require(scaleFactors.size == plotSpecList.size) { "plots count (${plotSpecList.size}) and scales count (${scaleFactors.size}) should be equal." }

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
                plotSpecList.zip(scaleFactors).forEach { (plotSpec, scaleFactor) ->
                    val image = PlotImageExport
                        .buildImageFromRawSpecs(
                            plotSpec = plotSpec,
                            format = FORMAT,
                            scaleFactor = scaleFactor
                        )

                    val titleTrimmed = Regex("[^a-z0-9_]").replace(title.toLowerCase(), "_")
                    val namePrexix = "${titleTrimmed}_scale_${scaleFactor}_"
                    val imgFile = createDemoFile(DEMO_PROJECT, namePrexix, FORMAT.defFileExt)
                    imgFile.writeBytes(image.bytes)
                    val imgSrc = imgFile.toURI()

                    div("demo") {
                        p { +"scaleFactor: $scaleFactor, DPI: ${image.DPI}" }
                        unsafe { +"<img src=\"$imgSrc\" width=\"${image.plotSize.x}\" height=\"${image.plotSize.y}\"/>" }
                    }
                }
            }
        }

        return writer.toString()
    }
}