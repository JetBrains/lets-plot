package jetbrains.datalore.visualization.plot.pythonExtension

import jetbrains.datalore.base.jsObject.mapToJsObjectInitializer

object PlotHtmlGen {
    fun getHtml(plotSpec: Map<String, Any>): String {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(6) { alphabet.random() }.joinToString("")
        val plotOutputId = "plot_output_$randomString"
        val plotSpecJs = mapToJsObjectInitializer(plotSpec)
        return """
            <div id="$plotOutputId"</div>
            <script type="text/javascript">
                var plotSpecList=[
                    ${plotSpecJs}    
                ];
                
                plotSpecList.forEach(function (spec, index) {
                   requirejs(['visualization-plot-config'], function(module) {
                        var plots = ${'$'}( "#$plotOutputId" ).get(0);
                        var plotContainer = document.createElement('div');
                        plots.appendChild(plotContainer);
                    
                        module.jetbrains.datalore.visualization.plot.MonolithicJs.buildPlotFromProcessedSpecs(spec, 440.0, 340.0, plotContainer);
                    });
                });
            </script>
        """
    }
}