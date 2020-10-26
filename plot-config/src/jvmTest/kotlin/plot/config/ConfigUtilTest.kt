/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
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
                .put(Variable("id"), idList)
                .put(Variable("foo"), dataValues)
                .build()

        val map = DataFrame.Builder()
                .put(Variable("id"), idList)
                .put(Variable("lon"), listOf(13.0, 24.0, -65.0, 117.0))
                .put(Variable("lat"), listOf(42.0, 21.0, -12.0, 77.0))
                .build()

        val joinedDf = ConfigUtil.rightJoin(data, "id", map, "id")

        assertThat(joinedDf.variables().map { it.toString() })
                .containsExactlyInAnyOrder("id", "foo", "lon", "lat")

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

    @Test
    fun joinWithDuplicatedKeys() {
        val items = listOf(
            "State Debt", "Local Debt", "Gross State Product",
            "State Debt", "Local Debt", "Gross State Product",
            "State Debt", "Local Debt", "Gross State Product"
        )

        val state = listOf(
            "Alabama", "Alabama", "Alabama",
            "Alaska", "Alaska", "Alaska",
            "Arizona", "Arizona", "Arizona"
        )

        val value = listOf(
            10.7, 26.1, 228.0,
            5.9, 3.5, 55.7,
            13.3, 30.5, 361.1
        )

        val data = DataFrame.Builder()
            .put(Variable("item"), items)
            .put(Variable("state"), state)
            .put(Variable("value"), value)
            .build()


        val y = listOf(32.806671, 61.370716, 33.729759)
        val x = listOf(-86.79113000000001, -152.404419, -111.431221)
        val geoId = listOf("Alabama", "Alaska", "Arizona")

        val geo = DataFrame.Builder()
            .put(Variable("__x__"), x)
            .put(Variable("__y__"), y)
            .put(Variable("__geo_id__"), geoId)
            .build()

        val res = ConfigUtil.rightJoin(data, "state", geo, "__geo_id__")
        assertEquals(3, res.rowCount()) // TODO: should be 9, not 3
    }

}