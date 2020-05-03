/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import org.junit.Ignore
import kotlin.test.Test

class ScaleProviderHelperTest {

    @Test()
    fun nullValuesInDataFrame() {
        val region = DataFrame.Variable("region")
        val df = DataFrame.Builder()
            .put(region, listOf("Europe", "Asia", null, "Australia"))
            .build()

        ScaleProviderHelper.createDefault(Aes.MAP_ID).createScale(df, region)
    }
}
