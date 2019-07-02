package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.serialization.ImplicitReflectionSerializer
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

private const val DEMO_PROJECT = "visualization-plot-demo"
private const val OUT_DIR_JS = "$DEMO_PROJECT/build/demoWeb"

private const val MAIN_JS = "$DEMO_PROJECT.js"
private val LIBS_JS = listOf(
    "kotlin.js",
    "kotlin-logging.js",
    "kotlin-test.js",
    "kotlinx-serialization-kotlinx-serialization-runtime.js",

    "base.js",
    "mapper-core.js",
    "visualization-base-svg.js",
    "visualization-base-svg-mapper.js",
    "visualization-base-canvas.js",

    "visualization-plot-common.js",
    "visualization-plot-base.js",
    "visualization-plot-builder.js",
    "visualization-plot-config.js"
)


@ImplicitReflectionSerializer
fun main() {
    val projectRoot = getProjectRoot()
    println("Project root: $projectRoot")
    val tmpDir = File(projectRoot, OUT_DIR_JS)
    val file = File.createTempFile("index", ".html", tmpDir)
    println(file.canonicalFile)

    val html = genIndexHtml()
    FileWriter(file).use {
        it.write(html)
    }

    val desktop = Desktop.getDesktop()
    desktop.browse(file.toURI());
}

private fun getProjectRoot(): String {
    // works when launching from IDEA
    val projectRoot = System.getenv()["PWD"] ?: throw IllegalStateException("'PWD' env variable is not defined")

    val rootProjectName = "datalore-plot"
    if (!projectRoot.contains(rootProjectName)) {
        throw IllegalStateException("'PWD' is not pointing to $rootProjectName : $projectRoot")
    }

    return projectRoot
}

@ImplicitReflectionSerializer
private fun genIndexHtml(): String {
    val plotSpecListJs = StringBuilder("[\n")
    with(BarPlot()) {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
        var first = true
        for (spec in plotSpecList) {
            if (!first) plotSpecListJs.append(',') else first = false
            plotSpecListJs.append(toJsObjectInitializer(spec))
        }
    }
    plotSpecListJs.append("\n]")

    val writer = StringWriter().appendHTML().html {
        lang = "en"
        head {
            title("SVG - DOM mapper demo")
        }
        body {
            div { id = "root" }

            for (lib in LIBS_JS) {
                script {
                    type = "text/javascript"
                    src = "lib/$lib"
                }
            }

            script {
                type = "text/javascript"
                src = MAIN_JS
            }

            script {
                type = "text/javascript"
                unsafe {
                    +"""
                        |var plotSpecList=$plotSpecListJs;
                        |window.console.log('Iterate specs ...');
                        |plotSpecList.forEach(function (item, index) {
                        |   console.log('Index: ['+index+']');
                        |   console.log(item);
                        |   
                        |   visualization-plot-demo.aaa12345(item)
                        |});
                    """.trimMargin()

                }
            }
        }
    }

    return writer.toString()
}

private fun toJsObjectInitializer(spec: Map<String, Any>): String {
    val buffer = StringBuilder()

    var handleValue: (v: Any?) -> Unit = {}
    val handleList = { list: List<*> ->
        buffer.append('[')
        var first = true
        for (v in list) {
            if (!first) buffer.append(',') else first = false
            handleValue(v)
        }
        buffer.append(']')
    }
    val handleMap = { map: Map<*, *> ->
        buffer.append('{')
        var first = true
        for ((k, v) in map) {
            if (!first) buffer.append(',') else first = false
            buffer.append('\n')
            buffer.append(k).append(':')
            handleValue(v)
        }

        buffer.append("\n}")
    }
    handleValue = { v: Any? ->
        when (v) {
            is String -> buffer.append('"').append(v).append('"')
            is Boolean, Double, Long, Float, Int, Short, Byte -> buffer.append(v)
            null -> buffer.append("null")
            is List<*> -> handleList(v)
            is Map<*, *> -> handleMap(v)
            else -> throw IllegalArgumentException("Can't serialize object $v")
        }
    }

    handleMap(spec)
    return buffer.toString()
}


