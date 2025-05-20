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
                """\(2^a x^2\)""",
                """\((1 + 2)(2^2 - (2 - 10^0))\)""",
                """\(-2^{3^{5_7}}}\)-11""",
                """-2·\(3^{-13}\) + 7""",
                """a·\(b^{c \quad d \, e \: f \  g \qquad h} + A\)""",
                """A\(^{-B \cdot C}\)""",
                """\((\alpha^{-\infty \gamma} - 1) / (\theta + \eta ) \)""",
            )),
            getPlotSpec("""Index: \\(a_b\\)""", listOf(
                """\(2_a x_2\)""",
                """\((1 + 2)(2_2 - (2 - 1_\kappa))\)""",
                """\(-2_{3_{5^7}}\)-11""",
                """-2·\(3_{-13}\) + 7""",
                """a·\(b_{c \quad d \, e \: f \  g \qquad h} + A\)""",
                """A\(_{-B \cdot C}\)""",
                """\((\alpha_{-\infty \gamma} - 1) / (\theta + \eta ) \)""",
            )),
            getPlotSpec("Symbols", listOf(
                """\(\alpha\) and \(\Alpha\)""",
                """\(\beta\) and \(\Beta\)""",
                """\(\gamma\) and \(\Gamma\)""",
                """\( \chi \) and \( \Chi \)""",
                """\( \psi \) and \( \Psi \)""",
                """\( \omega \) and \( \Omega \)""",
                """\( \pm \) and \( \neq \) and \( \unknown \)""",
            )),
            getPlotSpec("Fractions", listOf(
                """\( \frac{a}{b} \)""",
                """\( a + \frac{b}{c} \)""",
                """\( \frac{a}{b} + c \)""",
                """\( \frac{a}{b} + \frac{c}{d} \)""",
                """\( \frac{a + 1}{b} + \frac{c}{d + 1} \)""",
                """\( 10^{1000} - \frac{\Alpha^\beta - \gamma_\Delta}{\Omega} - \frac{\omega}{\alpha_\Beta - \Gamma^\delta} - 1000^{10} \)""",
                """\( \alpha^\frac{1 + 2}{3} + \beta_{\frac{4}{5 + 6}} \)""",
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