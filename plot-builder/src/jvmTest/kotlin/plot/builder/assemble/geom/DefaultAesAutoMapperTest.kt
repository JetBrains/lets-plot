/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMIN
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMIN
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultAesAutoMapperTest {

    @Test
    fun geomPointShouldMapToDefaultVariables() {
        assertEquals(
                listOf(POINT_X, POINT_Y),
            jetbrains.datalore.plot.builder.assemble.geom.DefaultAesAutoMapperTest.Companion.getMappedLabelsForAes(
                Aes.X,
                Aes.Y
            )
        )
    }

    @Test
    fun geomRectShouldMapToDefaultVariables() {
        assertEquals(
                listOf(RECT_XMIN, RECT_YMIN, RECT_XMAX, RECT_YMAX),
            jetbrains.datalore.plot.builder.assemble.geom.DefaultAesAutoMapperTest.Companion.getMappedLabelsForAes(
                Aes.XMIN,
                Aes.YMIN,
                Aes.XMAX,
                Aes.YMAX
            )
        )
    }

    companion object {
        private val DATA_FRAME = DataFrame.Builder()
                .put(Variable("foo"), listOf(1.0, 2.0))
                .put(Variable(POINT_Y), listOf(23.0, 13.0))
                .put(Variable(POINT_X), listOf(42.0, 17.0))
                .put(Variable("bar"), listOf(3.0, 4.0))
                .put(Variable(RECT_YMAX), listOf(4.0, 8.0))
                .put(Variable(RECT_YMIN), listOf(3.0, 6.0))
                .put(Variable("baz"), listOf(5.0, 6.0))
                .put(Variable(RECT_XMIN), listOf(1.0, 5.0))
                .put(Variable(RECT_XMAX), listOf(2.0, 7.0))
                .build()

        private fun getMappedLabelsForAes(vararg aes: Aes<*>): List<String> {
            val aesAutoMapper =
                DefaultAesAutoMapper(listOf(*aes)) { false }
            val mappings = aesAutoMapper.createMapping(jetbrains.datalore.plot.builder.assemble.geom.DefaultAesAutoMapperTest.Companion.DATA_FRAME)
            return aes.map { aesItem -> mappings.getValue(aesItem).name }
        }
    }
}