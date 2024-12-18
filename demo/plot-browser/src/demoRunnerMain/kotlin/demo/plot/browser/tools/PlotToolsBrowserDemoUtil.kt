/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.tools

import demo.common.util.demoUtils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.jsObject.JsObjectSupportCommon.mapToJsObjectInitializer
import java.io.StringWriter

internal object PlotToolsBrowserDemoUtil {
    private const val DEMO_PROJECT = "demo/plot"
    private const val ROOT_ELEMENT_ID = "root"

    fun show(
        title: String,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector = DoubleVector(1000.0, 600.0),
        applyBackendTransform: Boolean = true,
        backgroundColor: String = "lightgrey"
    ) {
        BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
            getHtml(
                title,
                listOf(plotSpec),
                plotSize,
                applyBackendTransform,
                backgroundColor
            )
        }
    }

    private fun getPlotLibPath(): String {
        return BrowserDemoUtil.getPlotLibPath()
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector,
        applyBackendTransform: Boolean,
        backgroundColor: String
    ): String {

        val plotFun = if (applyBackendTransform) {  // see: MonolithicJs
            "buildPlotFromProcessedSpecs"
        } else {
            "buildPlotFromRawSpecs"
        }

        val plotSpecJs = mapToJsObjectInitializer(plotSpecList.first())

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
                                background-color:$backgroundColor
                            }
                        """.trimIndent()
                    }
                }
            }
            body {
                script {
                    type = "text/javascript"
                    src = getPlotLibPath()
                }

                div("demo") {
                    id = ROOT_ELEMENT_ID
//                    button() {
//                        id = "zoom-tool-btn"
//                        text("Zoom Off")
//                    }
                }

                script {
                    type = "text/javascript"
                    unsafe {
                        +"""
                        |
                        |var plotSpec = $plotSpecJs;
                        |var rootElement = document.getElementById("root");
                        |
                        |// Toolbar
                        |var toolbar = new LetsPlot.tools.DefaultToolbar();
                        |rootElement.appendChild(toolbar.getElement());
                        |
                        |var parentElement = document.createElement('div');
                        |rootElement.appendChild(parentElement);
                        |var fig = LetsPlot.$plotFun(plotSpec, ${plotSize.x}, ${plotSize.y}, parentElement);
                        |
                        |toolbar.bind(fig);
                        |
                    """.trimMargin()

                    }
                }
            }
        }

        return writer.toString()
    }
}

//|
//|
//|var plotSpecList=$plotSpecListJs;
//|plotSpecList.forEach(function (spec, index) {
//    |
//    |   var parentElement = document.createElement('div');
//    |   document.getElementById("root").appendChild(parentElement);
//    |   LetsPlot.$plotFun(spec, ${plotSize.x}, ${plotSize.y}, parentElement);
//    |});


//|// Toolbar
//|
//|const zoom_tool = {
//    |   'active' : false,
//    |   'name' : 'my-box-zoom',
//    |   'interaction' : {'name':'box-zoom', 'dimension': 'both'},
//    |   'on-event' : 'to-do'
//    |};
//|const zoom_btn = document.getElementById('zoom-tool-btn');
//|zoom_btn.addEventListener('click', function() {
//    |  zoom_tool.active = !zoom_tool.active;
//    |  if (zoom_tool.active) {
//        |    fig.startInteraction(zoom_tool.interaction);
//        |    zoom_btn.innerText = 'Zoom On';
//        |  } else {
//        |    fig.abortInteraction(zoom_tool.interaction.name);
//        |    zoom_btn.innerText = 'Zoom Off';
//        |  }
//    |});
//|
//|// Event dispatcher
//|var toolEventHandler = function(e) {
//    |  console.log('Event triggered:', e);
//    |};
//|
//|fig.onToolEvent(toolEventHandler)
