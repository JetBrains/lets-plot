/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class LaTeX {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            inTitle("""y = \\frac{x^2}{2}"""),
            inTitle("""y = \\log_{10}(x)"""),
        )
    }

    private fun inTitle(formula: String): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Formula: \\($formula\\)'
              },
              'layers': [
                {
                  'geom': 'point',
                  'x': 0,
                  'y': 0
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}