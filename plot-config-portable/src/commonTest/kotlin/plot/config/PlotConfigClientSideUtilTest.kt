/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

// TODO: don't test private functions
/*
class PlotConfigClientSideUtilTest {

    @Test
    fun shouldShowAxisTooltip() {
        val layerSettingsList = ArrayList<Pair<GeomKind, StatKind>>()

        GeomKind.values()
            .filter { geomKind -> !WITHOUT_AXIS_TOOLTIP.contains(geomKind) }
            .forEach { geomKind -> layerSettingsList.add(Pair(geomKind,
                StatKind.IDENTITY
            )) }

        listOf(
            StatKind.CONTOUR,
            StatKind.CONTOURF,
            StatKind.DENSITY2D
        )
            .forEach { statKind -> layerSettingsList.add(Pair(GeomKind.PATH, statKind)) }

        assertAxisTooltipEnabled(layerSettingsList, true)
    }

    @Test
    fun shouldNotShowAxisTooltip() {
        val layerSettingsList = ArrayList<Pair<GeomKind, StatKind>>()

        WITHOUT_AXIS_TOOLTIP
            .forEach { geomKind ->
                layerSettingsList.add(Pair(geomKind,
                    StatKind.IDENTITY
                ))
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

            val builder =
                GeomInteractionUtil.createGeomInteractionBuilder(
                    emptyList(),
                    geomKind,
                    statKind,
                    false
                )
            assertEquals(isAxisTooltipEnabled, builder.isAxisTooltipEnabled, "$geomKind, $statKind")
        }
    }

    companion object {
        private val WITHOUT_AXIS_TOOLTIP = listOf(GeomKind.PATH, GeomKind.MAP, GeomKind.DENSITY2DF, GeomKind.CONTOURF, GeomKind.POLYGON, GeomKind.TILE, GeomKind.BIN_2D, GeomKind.RECT, GeomKind.H_LINE)
    }
}
*/