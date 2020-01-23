/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.builder.map.GeoPositionField
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_ID_COLUMN
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
                .put(Variable(GeoPositionField.DATA_JOIN_KEY_COLUMN), idList)
                .put(Variable(MAP_JOIN_ID_COLUMN), dataValues)
                .build()

        val map = DataFrame.Builder()
                .put(Variable(MAP_JOIN_ID_COLUMN), idList)
                .put(Variable("lon"), listOf(13.0, 24.0, -65.0, 117.0))
                .put(Variable("lat"), listOf(42.0, 21.0, -12.0, 77.0))
                .build()

        val joinedDf = ConfigUtil.rightJoin(data, GeoPositionField.DATA_JOIN_KEY_COLUMN, map, MAP_JOIN_ID_COLUMN)

        assertThat(joinedDf.variables().map { it.toString() })
                .containsExactlyInAnyOrder(GeoPositionField.DATA_JOIN_KEY_COLUMN, MAP_JOIN_ID_COLUMN, "lon", "lat")

        var dataIdVar: Variable? = null
        for (variable in joinedDf.variables()) {
            if (MAP_JOIN_ID_COLUMN == variable.name) {
                dataIdVar = variable
                break
            }
        }

        assertNotNull(dataIdVar)
        assertEquals(joinedDf[dataIdVar], dataValues)
    }
}