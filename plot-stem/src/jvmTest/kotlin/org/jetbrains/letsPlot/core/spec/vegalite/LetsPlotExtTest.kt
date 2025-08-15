/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.BackendTestUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Test

class LetsPlotExtTest {

    @Test
    fun `spec augmentation`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "mark": "point",
                |  
                |  "data": { "values": [ { "a": 1, "b": 2 } ]  },
                |  "encoding": { 
                |    "x": { "field": "a" },
                |    "y": { "field": "b" }
                |  },
                |  "title": "My Chart",
                |  "letsPlotExt": { 
                |    "specAugmentation": { "theme": { "flavor": "darcula" } }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(Option.Plot.THEME)).isEqualTo(
            mapOf(
                Option.Theme.FLAVOR to ThemeOption.Flavor.DARCULA,
            )
        )
    }
}
