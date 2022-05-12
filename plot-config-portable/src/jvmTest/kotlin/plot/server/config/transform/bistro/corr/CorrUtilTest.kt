/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.corr

import org.junit.Test

internal class CorrUtilTest {

    @Test
    fun `should not fail with empty coefficients`() {
        CorrUtil.correlationsFromCoefficients(
            mapOf()
        )
    }


    @Test
    fun `should not fail with null coefficients`() {
        CorrUtil.correlationsFromCoefficients(
            mapOf(
                "a" to listOf(1.0, 0.5, null),
                "b" to listOf(0.5, 1.0, 0.2),
                "c" to listOf(null,0.2, 1.0)
            )
        )
    }

    @Test
    fun `coefficients greater than one have no special handling`() {
        CorrUtil.correlationsFromCoefficients(
            mapOf(
                "a" to listOf(1.0, 1.5, 5.0),
                "b" to listOf(1.5, 1.0, 2.2),
                "c" to listOf(5.0,2.2, 1.0)
            )
        )
    }
}
