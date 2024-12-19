/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

class SpecialSymbols {
    fun plotSpec(): MutableMap<String, Any> {
        return  mutableMapOf(
            "kind" to "plot",
            "mapping" to mapOf(
                "x" to listOf("""< & ' " \ / > ®"""),
                "y" to listOf(1.0)
            ),
            "layers" to listOf(
                mapOf(
                    "geom" to "bar",
                    "alpha" to 0.5
                )
            )
        )
    }
}
