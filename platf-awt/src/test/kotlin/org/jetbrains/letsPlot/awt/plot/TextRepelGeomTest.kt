/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test

class TextRepelGeomTest: VisualPlotTestBase(expectedImagesSubdir = "geoms") {

    data class PointRow(
        val x: Double,
        val y: Double,
        val text: String
    )

    @Test
    fun `text outside the point cloud`() {
        val rows = generatePointCloudWithThreeLabels(
            n = 4000,
            seed = 0
        )
        val labeled = rows.filter { it.text.isNotEmpty() }

        val allX = jsonNumberArray(rows.map { it.x })
        val allY = jsonNumberArray(rows.map { it.y })
        val allText = jsonStringArray(rows.map { it.text })

        val labeledX = jsonNumberArray(labeled.map { it.x })
        val labeledY = jsonNumberArray(labeled.map { it.y })
        val labeledText = jsonStringArray(labeled.map { it.text })

        val spec = parsePlotSpec(
            """
            |{
            |  "mapping": {},
            |  "data_meta": {},
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "data": {
            |        "x": $allX,
            |        "y": $allY,
            |        "text": $allText
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "str", "column": "text" }
            |        ]
            |      },
            |      "color": "grey"
            |    },
            |    {
            |      "geom": "text_repel",
            |      "data": {
            |        "x": $allX,
            |        "y": $allY,
            |        "text": $allText
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "label": "text"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "str", "column": "text" }
            |        ]
            |      },
            |      "seed": 42,
            |      "max_time": -1,
            |      "max_iter": 200,
            |      "max_overlaps": -1.0
            |    },
            |    {
            |      "geom": "point",
            |      "data": {
            |        "x": $labeledX,
            |        "y": $labeledY,
            |        "text": $labeledText
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "str", "column": "text" }
            |        ]
            |      },
            |      "color": "red"
            |    }
            |  ],
            |  "metainfo_list": []
            |}
            """.trimMargin()
        )

        assertPlot("text_outside_the_point_cloud.png", spec)
    }

    // ---------------- helpers ----------------

    @Suppress("SameParameterValue")
    private fun generatePointCloudWithThreeLabels(
        n: Int,
        seed: Int
    ): List<PointRow> {
        require(n >= 3) { "n must be >= 3" }

        val random = Random(seed)
        val texts = MutableList(n) { "" }

        val labelIndices = (0 until n).shuffled(random).take(3)
        val labels = listOf("label_1", "label_2", "label_3")
        for ((i, idx) in labelIndices.withIndex()) {
            texts[idx] = labels[i]
        }

        return List(n) { i ->
            PointRow(
                x = nextGaussian(random),
                y = nextGaussian(random),
                text = texts[i]
            )
        }
    }

    // Box-Muller
    private fun nextGaussian(random: Random): Double {
        var u1 = 0.0
        while (u1 == 0.0) {
            u1 = random.nextDouble()
        }
        val u2 = random.nextDouble()
        return sqrt(-2.0 * ln(u1)) * cos(2.0 * Math.PI * u2)
    }

    private fun jsonNumberArray(values: List<Double>, digits: Int = 6): String {
        val body = values.joinToString(", ") { v ->
            "%.${digits}f".format(java.util.Locale.US, v)
        }
        return "[ $body ]"
    }

    private fun jsonStringArray(values: List<String>): String {
        val body = values.joinToString(", ") { s -> "\"${escapeJson(s)}\"" }
        return "[ $body ]"
    }

    private fun escapeJson(s: String): String {
        return buildString(s.length + 8) {
            for (ch in s) {
                when (ch) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(ch)
                }
            }
        }
    }
}
