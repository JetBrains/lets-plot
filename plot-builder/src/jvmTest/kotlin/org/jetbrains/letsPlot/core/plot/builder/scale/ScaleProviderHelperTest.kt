/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import kotlin.test.Test

class ScaleProviderHelperTest {

    @Test()
    fun nullValuesInDataFrame() {
        val region = DataFrame.Variable("region")
        val df = DataFrame.Builder()
            .put(region, listOf("Europe", "Asia", null, "Australia"))
            .build()

        ScaleProviderHelper.createDefault(Aes.HJUST).createScale(
            "region",
            DiscreteTransform(df.distinctValues(region), emptyList()),
            guideTitle = null
        )
    }
}
