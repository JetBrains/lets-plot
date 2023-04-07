/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package jetbrains.datalore.plot

import kotlin.test.Test

class PlotImageExportTest {

    @Test
    fun specialSymbols() {
        val spec = mutableMapOf(
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
        PlotImageExport.buildImageFromRawSpecs(spec, PlotImageExport.Format.PNG, 1.0, 144.0)
    }
}
