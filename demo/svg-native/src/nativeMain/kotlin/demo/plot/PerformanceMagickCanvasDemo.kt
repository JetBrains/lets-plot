/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import demo.savePlot
import demoAndTestShared.parsePlotSpec
import kotlinx.cinterop.ExperimentalForeignApi

object PerformanceMagickCanvasDemo {
    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        savePlot(basic(), "performance.bmp")
    }

    fun basic(): MutableMap<String, Any> {
        val n = 100
        val xs = List(n) { it } .joinToString(", ")
        val ys = List(n) { 0 }.joinToString(", ")
        val data = """ { "x": [$xs], "y": [$ys] }"""
        val spec = """
            |{
            |  "data": $data,
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "y": "y" }
            |    }
            |  ]
            |}               
        """.trimMargin()

        return parsePlotSpec(spec)
    }
}
