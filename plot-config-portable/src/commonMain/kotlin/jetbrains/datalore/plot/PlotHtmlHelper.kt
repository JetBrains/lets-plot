/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.jsObject.JsObjectSupport
import jetbrains.datalore.base.random.RandomString.randomString
import jetbrains.datalore.plot.server.config.PlotConfigServerSide

object PlotHtmlHelper {
    // Data-attributes used to store extra information about the meaning of 'script' elements
    // See also: python-package/lets_plot/frontend_context/_jupyter_notebook_ctx.py
    // Duplication?
    private const val ATT_SCRIPT_KIND = "data-lets-plot-script"
    private const val SCRIPT_KIND_LIB_LOADING = "library"
    private const val SCRIPT_KIND_PLOT = "plot"

    fun scriptUrl(
        version: String,
        suffix: String = "min.js",
        baseUrl: String = "https://dl.bintray.com/jetbrains/lets-plot"
    ) = "$baseUrl/lets-plot-$version.$suffix"

    fun getDynamicConfigureHtml(scriptUrl: String, verbose: Boolean): String {
        val outputId = randomString(6)

        val successMessage = if (verbose) {
            """
            |   var div = document.createElement("div");
            |   div.style.color = 'darkblue';
            |   div.textContent = 'Lets-Plot JS successfully loaded.';
            |   document.getElementById("$outputId").appendChild(div);
            """.trimMargin()
        } else ""

        return """
            |   <div id="$outputId"></div>
            |   <script type="text/javascript" $ATT_SCRIPT_KIND="$SCRIPT_KIND_LIB_LOADING">
            |       if(!window.letsPlotCallQueue) {
            |           window.letsPlotCallQueue = [];
            |       }; 
            |       window.letsPlotCall = function(f) {
            |           window.letsPlotCallQueue.push(f);
            |       };
            |       (function() {
            |           var script = document.createElement("script");
            |           script.type = "text/javascript";
            |           script.src = "$scriptUrl";
            |           script.onload = function() {
            |               window.letsPlotCall = function(f) {f();};
            |               window.letsPlotCallQueue.forEach(function(f) {f();});
            |               window.letsPlotCallQueue = [];
            |               
            |               $successMessage
            |           };
            |           script.onerror = function(event) {
            |               window.letsPlotCall = function(f) {};
            |               window.letsPlotCallQueue = [];
            |               var div = document.createElement("div");
            |               div.style.color = 'darkred';
            |               div.textContent = 'Error loading Lets-Plot JS';
            |               document.getElementById("$outputId").appendChild(div);
            |           };
            |           var e = document.getElementById("$outputId");
            |           e.appendChild(script);
            |       })();
            |   </script>
        """.trimMargin()
    }

    fun getDynamicDisplayHtmlForRawSpec(plotSpec: MutableMap<String, Any>, size: DoubleVector? = null): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        val plotSpecJs = JsObjectSupport.mapToJsObjectInitializer(plotSpec)
        return getDynamicDisplayHtml(plotSpecJs, size)
    }

    private fun getDynamicDisplayHtml(plotSpecAsJsObjectInitializer: String, size: DoubleVector?): String {
        val outputId = randomString(6)
        val dim = if (size == null) "-1, -1" else "${size.x}, ${size.y}"
        return """
            |   <div id="$outputId"></div>
            |   <script type="text/javascript" $ATT_SCRIPT_KIND="$SCRIPT_KIND_PLOT">
            |       (function() {
            |           var plotSpec=$plotSpecAsJsObjectInitializer;
            |           var plotContainer = document.getElementById("$outputId");
            |           window.letsPlotCall(function() {{
            |               LetsPlot.buildPlotFromProcessedSpecs(plotSpec, ${dim}, plotContainer);
            |           }});
            |       })();    
            |   </script>
        """.trimMargin()
    }

    fun getStaticConfigureHtml(scriptUrl: String): String {
        return "<script type=\"text/javascript\" $ATT_SCRIPT_KIND=\"$SCRIPT_KIND_LIB_LOADING\" src=\"$scriptUrl\"></script>"
    }

    fun getStaticDisplayHtmlForRawSpec(plotSpec: MutableMap<String, Any>, size: DoubleVector? = null): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        val plotSpecJs = JsObjectSupport.mapToJsObjectInitializer(plotSpec)
        return getStaticDisplayHtml(plotSpecJs, size)
    }

    private fun getStaticDisplayHtml(
        plotSpecAsJsObjectInitializer: String,
        size: DoubleVector?
    ): String {
        val outputId = randomString(6)
        val dim = if (size == null) "-1, -1" else "${size.x}, ${size.y}"
        return """
            |   <div id="$outputId"></div>
            |   <script type="text/javascript" $ATT_SCRIPT_KIND="$SCRIPT_KIND_PLOT">
            |       var plotSpec=$plotSpecAsJsObjectInitializer;
            |       var plotContainer = document.getElementById("$outputId");
            |       LetsPlot.buildPlotFromProcessedSpecs(plotSpec, ${dim}, plotContainer);
            |   </script>
        """.trimMargin()
    }
}