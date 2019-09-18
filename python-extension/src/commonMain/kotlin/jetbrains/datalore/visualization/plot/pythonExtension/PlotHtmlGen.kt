package jetbrains.datalore.visualization.plot.pythonExtension

object PlotHtmlGen {
    val KOTLIN_LIBS = listOf(
        "kotlin.js",
        "kotlin-logging.js"
    )

    val BASE_MAPPER_LIBS = listOf(
        "datalore-plot-base-portable.js",          // base-portable
        "datalore-plot-base.js",                   // base
        "mapper-core.js",
        "visualization-base-svg.js",
        "visualization-base-svg-mapper.js"
    )

    val PLOT_LIBS = listOf(
        "visualization-base-canvas.js",     // required by plot-builder (get rid?)
        "visualization-plot-common-portable.js",
        "visualization-plot-common.js",
        "visualization-plot-base-portable.js",
        "visualization-plot-base.js",
        "visualization-plot-builder-portable.js",
        "visualization-plot-builder.js",
        "visualization-plot-config-portable.js",
        "visualization-plot-config.js"
    )

    private const val ROOT_PROJECT = "datalore-plot"

//    fun openInBrowser(demoProject: String, html: () -> String) {
//        val outputDir = "$demoProject/build/demoWeb"
//
//        val projectRoot = getProjectRoot()
//        println("Project root: $projectRoot")
//        val tmpDir = File(projectRoot, outputDir)
//        val file = File.createTempFile("index", ".html", tmpDir)
//        println(file.canonicalFile)
//
//        FileWriter(file).use {
//            it.write(html())
//        }
//
//        val desktop = Desktop.getDesktop()
//        desktop.browse(file.toURI());
//    }

//    private fun getProjectRoot(): String {
//        // works when launching from IDEA
//        val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")
//
//        if (!projectRoot.contains(ROOT_PROJECT)) {
//            throw IllegalStateException("'PWD' is not pointing to $ROOT_PROJECT : $projectRoot")
//        }
//        return projectRoot
//    }


    fun mapperDemoHtml(demoProject: String, callFun: String, libs: List<String>, title: String): String {
//        val mainScript = "$demoProject.js"
//        val writer = StringWriter().appendHTML().html {
//            lang = "en"
//            head {
//                title(title)
//            }
//            body {
//                div { id = "root" }
//
//                for (lib in libs) {
//                    script {
//                        type = "text/javascript"
//                        src = "lib/$lib"
//                    }
//                }
//
//                script {
//                    type = "text/javascript"
//                    src = mainScript
//                }
//
//                script {
//                    type = "text/javascript"
//                    unsafe {
//                        +"""
//                        |
//                        |   window['$demoProject'].$callFun();
//                    """.trimMargin()
//
//                    }
//                }
//            }
//        }
//
//        return writer.toString()
        return ""
    }

//    fun getHtml(plotSpec: Map<String, Any>): String {
//        return "<b>PlotHtmlGen: hello Jupyter!</b>"
//    }

    fun getHtml(plotSpec: Map<String, Any>): String {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(6) { alphabet.random() }.joinToString("")
        val plotOutputId = "plot_output_$randomString"

        val baseUrl = "http://0.0.0.0:8080"

        return """
    <div id="$plotOutputId"</div>


    <script type="text/javascript">
    requirejs.config({
        paths: {
            'kotlin': "$baseUrl/kotlin",
            'kotlin-logging': "$baseUrl/kotlin-logging",                   
            'datalore-plot-base-portable': "$baseUrl/datalore-plot-base-portable",      
            'datalore-plot-base': "$baseUrl/datalore-plot-base",               
            'mapper-core': "$baseUrl/mapper-core",                      
            'visualization-base-svg': "$baseUrl/visualization-base-svg",           
            'visualization-base-svg-mapper': "$baseUrl/visualization-base-svg-mapper",    
            'visualization-base-canvas': "$baseUrl/visualization-base-canvas",        
            'visualization-plot-common-portable': "$baseUrl/visualization-plot-common-portable",
            'visualization-plot-common': "$baseUrl/visualization-plot-common",        
            'visualization-plot-base-portable': "$baseUrl/visualization-plot-base-portable",  
            'visualization-plot-base': "$baseUrl/visualization-plot-base",          
            'visualization-plot-builder-portable': "$baseUrl/visualization-plot-builder-portable",
            'visualization-plot-builder': "$baseUrl/visualization-plot-builder",       
            'visualization-plot-config-portable': "$baseUrl/visualization-plot-config-portable",
            'visualization-plot-config': "$baseUrl/visualization-plot-config"        
        }
    });
</script>
    
    <script type="text/javascript">var plotSpecList=[
{
'mapping':{
'x':"time",
'y':"..count..",
'fill':"..count.."
},
'data':{
},
'scales':[{
'aesthetic':"fill",
'discrete':true,
'scale_mapper_kind':"color_hue"
}],
'layers':[{
'data':{
'..count..':[2.0,3.0],
'time':["Lunch","Dinner"]
},
'geom':"bar"
}]
}
];

plotSpecList.forEach(function (spec, index) {
   require(['visualization-plot-config'], function(plotConfig) {
        var plots = ${'$'}( "#$plotOutputId" ).get(0);
        var plotContainer = document.createElement('div');
        plots.appendChild(plotContainer);
    
        plotConfig.jetbrains.datalore.visualization.plot.MonolithicJs.buildPlotFromProcessedSpecs(spec, 440.0, 340.0, plotContainer);
	});
});</script>
"""
    }
}