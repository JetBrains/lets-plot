/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PlotConfigClientSideUtilTest {

    @Test
    fun shouldShowAxisTooltip() {
        val layerSettingsList = ArrayList<Pair<GeomKind, StatKind>>()

        GeomKind.values()
            .filter { geomKind -> !WITHOUT_AXIS_TOOLTIP.contains(geomKind) }
            .forEach { geomKind -> layerSettingsList.add(Pair(geomKind, StatKind.IDENTITY)) }

        listOf(StatKind.CONTOUR, StatKind.CONTOURF, StatKind.DENSITY2D)
            .forEach { statKind -> layerSettingsList.add(Pair(GeomKind.PATH, statKind)) }

        assertAxisTooltipEnabled(layerSettingsList, true)
    }

    @Test
    fun shouldNotShowAxisTooltip() {
        val layerSettingsList = ArrayList<Pair<GeomKind, StatKind>>()

        WITHOUT_AXIS_TOOLTIP
            .forEach { geomKind ->
                layerSettingsList.add(Pair<GeomKind, StatKind>(geomKind, StatKind.IDENTITY))
            }

        assertAxisTooltipEnabled(layerSettingsList, false)
    }

    private fun assertAxisTooltipEnabled(
        layerSettingsList: List<Pair<GeomKind, StatKind>>,
        isAxisTooltipEnabled: Boolean
    ) {
        for (settings in layerSettingsList) {
            val geomKind = settings.first
            val statKind = settings.second

            val builder = PlotConfigClientSideUtil.createGeomInteractionBuilder(emptyList(), geomKind, statKind, false)
            assertEquals(isAxisTooltipEnabled, builder.isAxisTooltipEnabled, "$geomKind, $statKind")
        }
    }

    companion object {
        private val WITHOUT_AXIS_TOOLTIP = listOf(PATH, MAP, DENSITY2DF, CONTOURF, POLYGON, BIN_2D, LIVE_MAP)
    }
}
