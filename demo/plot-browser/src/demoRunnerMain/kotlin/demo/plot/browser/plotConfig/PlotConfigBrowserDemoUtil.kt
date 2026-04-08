/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotConfig

import demo.common.utils.browser.BrowserDemoUtil
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.jsObject.JsObjectSupportCommon.mapToJsObjectInitializer
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.io.StringWriter

object PlotConfigBrowserDemoUtil {
    private const val DEMO_PROJECT_PATH = "demo/plot-browser"
    private const val ROOT_ELEMENT_ID = "root"


    // Entry point to explicitly show the JS Webpack build
    fun showJs(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector = DoubleVector(400.0, 300.0),
        applyBackendTransform: Boolean = true,
        backgroundColor: String = "lightgrey"
    ) {
        show(title, plotSpecList, plotSize, applyBackendTransform, backgroundColor, BrowserDemoUtil.Target.JS)
    }

    // Entry point to explicitly show the WasmJS Webpack build
    fun showWasm(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector = DoubleVector(400.0, 300.0),
        applyBackendTransform: Boolean = true,
        backgroundColor: String = "lightgrey"
    ) {
        show(title, plotSpecList, plotSize, applyBackendTransform, backgroundColor, BrowserDemoUtil.Target.WASM)
    }

    private fun show(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        plotSize: DoubleVector,
        applyBackendTransform: Boolean,
        backgroundColor: String,
        target: BrowserDemoUtil.Target = BrowserDemoUtil.Target.JS,
    ) {
        val htmlContent = getHtml(
            title,
            plotSpecList,
            target,
            plotSize,
            applyBackendTransform,
            backgroundColor
        )

        when (target) {
            BrowserDemoUtil.Target.JS -> BrowserDemoUtil.openInBrowserJs(DEMO_PROJECT_PATH) { htmlContent }
            BrowserDemoUtil.Target.WASM -> BrowserDemoUtil.openInBrowserWasm(DEMO_PROJECT_PATH) { htmlContent }
        }
    }

    private fun getHtml(
        title: String,
        plotSpecList: List<MutableMap<String, Any>>,
        target: BrowserDemoUtil.Target,
        plotSize: DoubleVector,
        applyBackendTransform: Boolean,
        backgroundColor: String
    ): String {

        val plotSpecListJs = StringBuilder("[\n")
        var first = true
        for (spec in plotSpecList) {
            @Suppress("NAME_SHADOWING")
            val spec = if (applyBackendTransform) MonolithicCommon.processRawSpecs(spec) else spec

            if (!first) plotSpecListJs.append(',') else first = false
            plotSpecListJs.append(mapToJsObjectInitializer(spec))
        }
        plotSpecListJs.append("\n]")

        val plotFun = if (applyBackendTransform) "buildPlotFromProcessedSpecs" else "buildPlotFromRawSpecs"

        val writer = StringWriter().appendHTML().html {
            lang = "en"
            head {
                title(title)
                style {
                    unsafe {
                        +"""
                            div.demo { border: 1px solid orange; margin: 20px; display: inline-block; }
                            body { background-color:$backgroundColor }
                        """.trimIndent()
                    }
                }
            }
            body {
                // Request the dynamically computed lib URL from the internal HTTP server
                script {
                    type = "text/javascript"
                    src = BrowserDemoUtil.getPlotLibUrl(target)
                }

                div("demo") { id = ROOT_ELEMENT_ID }

                // UPDATED: Promise-aware execution that handles Async WebAssembly loading
                script {
                    type = "text/javascript"
                    unsafe {
                        +"""
                        |window.addEventListener('load', function() {
                        |   
                        |   // 1. Wrap in Promise.resolve() because Wasm Webpack exports are often asynchronous!
                        |   Promise.resolve(window.LetsPlot).then(function(LetsPlotExport) {
                        |       var plotFunName = '$plotFun';
                        |       var actualPlotFun = null;
                        |       var targetContext = LetsPlotExport;
                        |       
                        |       // A. Try Direct export (Standard Kotlin/JS behavior)
                        |       if (LetsPlotExport && typeof LetsPlotExport[plotFunName] === 'function') {
                        |           actualPlotFun = LetsPlotExport[plotFunName];
                        |       } 
                        |       // B. Try Default export (Often happens in WasmJS Webpack bundles)
                        |       else if (LetsPlotExport && LetsPlotExport.default && typeof LetsPlotExport.default[plotFunName] === 'function') {
                        |           actualPlotFun = LetsPlotExport.default[plotFunName];
                        |           targetContext = LetsPlotExport.default;
                        |       } 
                        |       // C. Try Deep search (Sometimes Webpack nests it under the module name)
                        |       else {
                        |           for (var key in LetsPlotExport) {
                        |               if (LetsPlotExport[key] && typeof LetsPlotExport[key][plotFunName] === 'function') {
                        |                   actualPlotFun = LetsPlotExport[key][plotFunName];
                        |                   targetContext = LetsPlotExport[key];
                        |                   break;
                        |               }
                        |           }
                        |       }
                        |       
                        |       // D. Error handling if the function is completely missing
                        |       if (!actualPlotFun) {
                        |           console.error("Function " + plotFunName + " not found in window.LetsPlot! Did you add @JsExport?", LetsPlotExport);
                        |           document.getElementById("root").innerHTML = "<h3 style='color:red;'>Error: " + plotFunName + " not found in window.LetsPlot. Check console for details.</h3>";
                        |           return;
                        |       }
                        |
                        |       // E. Execute the rendering logic safely
                        |       var plotSpecList=$plotSpecListJs;
                        |       plotSpecList.forEach(function (spec, index) {
                        |           var parentElement = document.createElement('div');
                        |           document.getElementById("root").appendChild(parentElement);
                        |           const sizing = { width: ${plotSize.x}, height: ${plotSize.y} };
                        |           actualPlotFun.call(targetContext, spec, parentElement, sizing);
                        |       });
                        |       
                        |   }).catch(function(err) {
                        |       console.error("Error initializing WebAssembly module:", err);
                        |   });
                        |});
                    """.trimMargin()
                    }
                }
            }
        }
        return writer.toString()
    }
}