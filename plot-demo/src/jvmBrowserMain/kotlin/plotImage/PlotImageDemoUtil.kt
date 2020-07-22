/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.base.encoding.Base64
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.toPngDataUri
import jetbrains.datalore.plot.PlotImageExport
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.File
import java.io.StringWriter

object PlotImageDemoUtil {
    private const val DEMO_PROJECT = "plot-demo"
    private const val IMG_AS_DATA_URI = false
    private const val SCALE_FACTOR = 2.0
    private val FORMAT = PlotImageExport.PNG()

    fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
            getHtml(
                title,
                plotSpecList,
                plotSize,
                MutableList(plotSpecList.size) { SCALE_FACTOR }
            )
        }
    }

    // Render same plot with different scales
    fun showScaled(
        title: String,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector,
        scaleFactors: List<Double>
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
            getHtml(
                title,
                MutableList(scaleFactors.size) { plotSpec },
                plotSize,
                scaleFactors
            )
        }
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector,
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
                            plotSize = plotSize,
                            format = FORMAT,
                            scaleFactor = scaleFactor
                        )

                    val imgSrc = when(IMG_AS_DATA_URI) {
                        true -> toPngDataUri(Base64.encode(image))
                        false -> File.createTempFile("lets_plot_export", "img").apply { writeBytes(image) }.path
                    }

                    div("demo") {
                        if (scaleFactors.distinct().size != 1) {
                            p { + "scaleFactor: $scaleFactor" }
                        }
                        unsafe { + "<img src=\"$imgSrc\" width=\"${plotSize.x}\" height=\"${plotSize.y}\"/>" }
                    }
                }
            }
        }

        return writer.toString()
    }
}