/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.random.RandomString.randomString
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.commons.jsObject.JsObjectSupportCommon
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

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
            // - Build "dev" JS package (see js-package/README.md)
            // - Activate env containing Python.
            // $ python -m http.server 8080
            "http://127.0.0.1:8080/js-package/build/dist/js/developmentExecutable/lets-plot.js"
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

    fun getDynamicDisplayHtmlForRawSpec(plotSpec: MutableMap<String, Any>): String {
        return getDisplayHtmlForRawSpec(
            plotSpec,
            SizingPolicy.notebookCell(),
            dynamicScriptLoading = true,
            forceImmediateRender = false,
            responsive = false,
            removeComputationMessages = false,
            logComputationMessages = false,
        )
    }

    fun getStaticConfigureHtml(scriptUrl: String): String {
        return "<script type=\"text/javascript\" $ATT_SCRIPT_KIND=\"$SCRIPT_KIND_LIB_LOADING\" src=\"$scriptUrl\"></script>"
    }

    fun getStaticDisplayHtmlForRawSpec(
        plotSpec: MutableMap<String, Any>,
        size: DoubleVector? = null,
        removeComputationMessages: Boolean = false,
        logComputationMessages: Boolean = false
    ): String {
        val sizingPolicy = when (size) {
            null -> SizingPolicy.notebookCell()
            else -> SizingPolicy.fixed(size.x, size.y)
        }

        return getDisplayHtmlForRawSpec(
            plotSpec,
            sizingPolicy,
            dynamicScriptLoading = false,
            forceImmediateRender = false,
            responsive = false,
            removeComputationMessages = removeComputationMessages,
            logComputationMessages = logComputationMessages
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getDisplayHtmlForRawSpec(
        plotSpec: MutableMap<String, Any>,
        sizingPolicy: SizingPolicy,
        dynamicScriptLoading: Boolean,
        forceImmediateRender: Boolean,
        responsive: Boolean,
        removeComputationMessages: Boolean,
        logComputationMessages: Boolean
    ): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = MonolithicCommon.processRawSpecs(plotSpec)

        if (logComputationMessages) {
            PlotConfigUtil.findComputationMessages(plotSpec).forEach { LOG.info { "[when HTML generating] $it" } }
        }

        // Remove computation messages from the output
        if (removeComputationMessages) {
            PlotConfigUtil.removeComputationMessages(plotSpec)
        }

        val plotSpecJs = JsObjectSupportCommon.mapToJsObjectInitializer(plotSpec)
        return getDisplayHtmlForProcessedSpecs(
            plotSpecJs,
            sizingPolicy,
            dynamicScriptLoading = dynamicScriptLoading,
            forceImmediateRender = forceImmediateRender,
            responsive = responsive,
        )
    }

    private fun getDisplayHtmlForProcessedSpecs(
        plotSpecAsJsObjectInitializer: String,
        sizingPolicy: SizingPolicy,
        dynamicScriptLoading: Boolean,
        forceImmediateRender: Boolean,
        responsive: Boolean,
    ): String {
        val outputId = randomString(6)

        return """
        |   <div id="$outputId"></div>
        |   <script type="text/javascript" $ATT_SCRIPT_KIND="$SCRIPT_KIND_PLOT">
        |   
        |   (function() {
        |   // ----------
        |   
        |   const forceImmediateRender = ${forceImmediateRender};
        |   const responsive = ${responsive};
        |   
        |   let sizing = {
        |       width_mode: "${sizingPolicy.widthMode}",
        |       height_mode: "${sizingPolicy.heightMode}",
        |       width: ${sizingPolicy.width}, 
        |       height: ${sizingPolicy.height} 
        |   };
        |   
        |   const preferredWidth = document.body.dataset.letsPlotPreferredWidth;
        |   if (preferredWidth !== undefined) {
        |       sizing = {
        |           width_mode: 'FIXED',
        |           height_mode: 'SCALED',
        |           width: parseFloat(preferredWidth)
        |       };
        |   }
        |   
        |   const containerDiv = document.getElementById("$outputId");
        |   let fig = null;
        |   
        |   function renderPlot() {
        |       if (fig === null) {
        |           const plotSpec = $plotSpecAsJsObjectInitializer;
        |           ${
            if (dynamicScriptLoading)
                "window.letsPlotCall(function() { " +
                        "fig = LetsPlot.buildPlotFromProcessedSpecs(plotSpec, containerDiv, sizing); });"
            else
                "fig = LetsPlot.buildPlotFromProcessedSpecs(plotSpec, containerDiv, sizing);"
        }
        |       } else {
        |           fig.updateView({});
        |       }
        |   }
        |   
        |   const renderImmediately = 
        |       forceImmediateRender || (
        |           sizing.width_mode === 'FIXED' && 
        |           (sizing.height_mode === 'FIXED' || sizing.height_mode === 'SCALED')
        |       );
        |   
        |   if (renderImmediately) {
        |       renderPlot();
        |   }
        |   
        |   if (!renderImmediately || responsive) {
        |       // Set up observer for initial sizing or continuous monitoring
        |       var observer = new ResizeObserver(function(entries) {
        |           for (let entry of entries) {
        |               if (entry.contentBoxSize && 
        |                   entry.contentBoxSize[0].inlineSize > 0) {
        |                   if (!responsive && observer) {
        |                       observer.disconnect();
        |                       observer = null;
        |                   }
        |                   renderPlot();
        |                   if (!responsive) {
        |                       break;
        |                   }
        |               }
        |           }
        |       });
        |       
        |       observer.observe(containerDiv);
        |   }
        |   
        |   // ----------
        |   })();
        |   
        |   </script>
    """.trimMargin()
    }
}