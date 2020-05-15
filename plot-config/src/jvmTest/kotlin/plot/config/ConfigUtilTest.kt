/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.config.Option.Meta.MapJoin.ID
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConfigUtilTest {

    @Test
    fun rightJoinShouldNotRewriteLeftColumns() {
        val idList = listOf(0, 1, 2, 3)
        val dataValues = listOf("a", "b", "c", "d")

        val data = DataFrame.Builder()
                .put(Variable(ID), idList)
                .put(Variable("foo"), dataValues)
                .build()

        val map = DataFrame.Builder()
                .put(Variable(ID), idList)
                .put(Variable("lon"), listOf(13.0, 24.0, -65.0, 117.0))
                .put(Variable("lat"), listOf(42.0, 21.0, -12.0, 77.0))
                .build()

        val joinedDf = ConfigUtil.rightJoin(data, ID, map, ID)

        assertThat(joinedDf.variables().map { it.toString() })
                .containsExactlyInAnyOrder(ID, "foo", "lon", "lat")

        var dataVar: Variable? = null
        for (variable in joinedDf.variables()) {
            if ("foo" == variable.name) {
                dataVar = variable
                break
            }
        }

        assertNotNull(dataVar)
        assertEquals(dataValues, joinedDf[dataVar])
    }
}