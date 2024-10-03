/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class LatexElements {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            getPlotSpec("""Power degree: \\(a^b\\)""", listOf(
                """\(x^1\)""",
                """\(-2^3\)-5""",
                """-2·\(3^{-13}\) + 7""",
                """a·\(b^{cde}\)""",
                """A\(^{-B}\)""",
                """\(\alpha^{-\Delta}\)""",
            )),
            getPlotSpec("""Index: \\(a_b\\)""", listOf(
                """\(x_1\)""",
                """\(-2_3\)-5""",
                """-2·\(3_{-13}\) + 7""",
                """a·\(b_{cde}\)""",
                """A\(_{-B}\)""",
                """\(\alpha_{-\Delta}\)""",
            )),
            getPlotSpec("Greek alphabet", listOf(
                """\(\alpha\) and \(\Alpha\)""",
                """\(\beta\) and \(Beta\)""",
                """\(\gamma\) and \(Gamma\)""",
                """\(\chi\) and \(\Chi\)""",
                """\(\psi\) and \(\Psi\)""",
                """\(\omega\) and \(\Omega\)""",
            )),
        )
    }

    private fun getPlotSpec(titleFormula: String, formulas: List<String>): MutableMap<String, Any> {
        val spec = """
            {
              "mapping": {
                "y": "y",
                "label": "f"
              },
              "kind": "plot",
              "ggtitle": {
                "text": "$titleFormula"
              },
              "layers": [
                {
                  "geom": "text",
                  "x": 0.0,
                  "size": 10
                }
              ]
            }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "y" to formulas.indices.toList().sortedDescending(),
            "f" to formulas
        )
        return plotSpec
    }
}