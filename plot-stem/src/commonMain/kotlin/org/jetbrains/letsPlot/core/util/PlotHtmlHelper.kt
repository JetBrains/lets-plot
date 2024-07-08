/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.random.RandomString.randomString
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.FeatureSwitch.PLOT_VIEW_TOOLBOX_HTML
import org.jetbrains.letsPlot.core.commons.jsObject.JsObjectSupportCommon
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil

object PlotHtmlHelper {
    private val LOG = PortableLogging.logger(PlotHtmlHelper::class)

    // Data-attributes used to store extra information about the meaning of 'script' elements
    // See also: python-package/lets_plot/frontend_context/_jupyter_notebook_ctx.py
    // Duplication?
    private const val ATT_SCRIPT_KIND = "data-lets-plot-script"
    private const val SCRIPT_KIND_LIB_LOADING = "library"
    private const val SCRIPT_KIND_PLOT = "plot"

    /**
     * This method is used in Lets-Plot Kotlin API.
     */
    @Suppress("unused")
    fun scriptUrl(
        version: String
    ): String {
        val dev = version.contains("dev") || version.contains("SNAPSHOT")
        return if (dev) {
            // We don't publish "dev" version, it must be served on localhost:
            // $ cd lets-plot
            // $ python -m http.server 8080
            "http://127.0.0.1:8080/js-package/build/dist/js/developmentExecutable/js-package.js"
        } else {
            // bintray: until v2.0.2
//            "https://dl.bintray.com/jetbrains/lets-plot/lets-plot-$version.min.js"
            // cdnjs: v2.0.2
//            "https://cdnjs.cloudflare.com/ajax/libs/lets-plot/$version/lets-plot.min.js"
            // jsdelivr: since v2.0.3 (all prev versions shoul work as well)
            "https://cdn.jsdelivr.net/gh/JetBrains/lets-plot@v$version/js-package/distr/lets-plot.min.js"
        }
    }

    /**
     * This method is used in Lets-Plot Kotlin API.
     */
    @Suppress("unused")
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
        val plotSpec = SpecTransformBackendUtil.processTransform(plotSpec)
        val plotSpecJs = JsObjectSupportCommon.mapToJsObjectInitializer(plotSpec)
        return getDynamicDisplayHtml(plotSpecJs, size)
    }

    private fun getDynamicDisplayHtml(plotSpecAsJsObjectInitializer: String, size: DoubleVector?): String {
        val outputId = randomString(6)
        val dim = if (size == null) "-1, -1" else "${size.x}, ${size.y}"
        if(PLOT_VIEW_TOOLBOX_HTML) {
            // Experimental
            return """
            |   <div id="$outputId" style="background-color: orange;"></div>
            |   <script type="text/javascript" $ATT_SCRIPT_KIND="$SCRIPT_KIND_PLOT">
            |       (function() {
            |           var plotSpec=$plotSpecAsJsObjectInitializer;
            |           var containerDiv = document.getElementById("$outputId");
            |           window.letsPlotCall(function() {{
            |               var sizingPolicy = {
            |                           width_mode: "min",
            |                           height_mode: "scaled",
            |                           width: containerDiv.clientWidth
            |               };
            |               
            |               // Wrapper for toolbar and chart
            |               var outputDiv = document.createElement('div');
            |               outputDiv.setAttribute('style', 'display: inline-block;');
            |               containerDiv.appendChild(outputDiv);
            |           
            |               // Toolbar
            |               var toolbar = new LetsPlot.tools.SandboxToolbar();
            |               outputDiv.appendChild(toolbar.getElement());
            |               
            |               // Plot
            |               var plotContainer = document.createElement('div');
            |               outputDiv.appendChild(plotContainer);
            |               
            |               var options = {
            |                   sizing: sizingPolicy
            |               };
            |               var fig = LetsPlot.buildPlotFromProcessedSpecs(plotSpec, ${dim}, plotContainer, options);
            |               toolbar.bind(fig);
            |           }});
            |       })();
            |   </script>
        """.trimMargin()
        } else {
            // Production
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
    }

/*
This is for experimenting with responsive mode.

            |               var figModel = LetsPlot.buildPlotFromProcessedSpecs(plotSpec, ${dim}, plotContainer,
            |                   {
            |                   "sizing": {
            |                       "width_margin":100
            |                   }});
            |               console.info('info I: ' + figModel);
            |               var resizeHandler = function() {
            |                   console.info('info II: ' + figModel);
            |                   if(figModel) {
            |                       figModel.updateView();
            |                   }
            |               };
            |               window.addEventListener('resize', resizeHandler)

 */

    fun getStaticConfigureHtml(scriptUrl: String): String {
        return "<script type=\"text/javascript\" $ATT_SCRIPT_KIND=\"$SCRIPT_KIND_LIB_LOADING\" src=\"$scriptUrl\"></script>"
    }

    fun getStaticDisplayHtmlForRawSpec(plotSpec: MutableMap<String, Any>, size: DoubleVector? = null, removeComputationMessages: Boolean = false, logComputationMessages: Boolean = false): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = SpecTransformBackendUtil.processTransform(plotSpec)

        if (logComputationMessages) {
            PlotConfigUtil.findComputationMessages(plotSpec).forEach { LOG.info { "[when HTML generating] $it" } }
        }

        // Remove computation messages from the output
        if (removeComputationMessages) {
            PlotConfigUtil.removeComputationMessages(plotSpec)
        }

        val plotSpecJs = JsObjectSupportCommon.mapToJsObjectInitializer(plotSpec)
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