/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension

import jetbrains.datalore.base.jsObject.JsObjectSupport.mapToJsObjectInitializer
import jetbrains.datalore.plot.server.config.PlotConfigServerSide

internal object PlotHtmlGen {
    fun applyToRawSpecs(plotSpec: MutableMap<String, Any>): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        return applyToProcessedSpecs(plotSpec)
    }

    private fun applyToProcessedSpecs(plotSpec: MutableMap<String, Any>): String {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(6) { alphabet.random() }.joinToString("")
        val outputId = "plot_output_$randomString"
        val plotSpecJs = mapToJsObjectInitializer(plotSpec)
        return """
            <div id="$outputId"></div>
            <script type="text/javascript">
                (function() {
                    var plotSpec=$plotSpecJs;
                    var plotContainer = document.getElementById("$outputId");
                    window.letsPlotCall(function() {{
                        LetsPlot.buildPlotFromProcessedSpecs(plotSpec, -1, -1, plotContainer);
                    }});
                })();    
            </script>
        """
    }
}