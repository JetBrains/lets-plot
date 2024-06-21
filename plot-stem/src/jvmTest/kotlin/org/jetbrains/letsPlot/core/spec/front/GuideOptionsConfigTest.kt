/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.assemble.ColorBarOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptionsList
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideTitleOption
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendOptions
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ScaleConfig
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GuideOptionsConfigTest {

    @Test
    fun `choose title`() {
        val guideList = GuideOptionsList()

        guideList.add(LegendOptions(title = "title 1"))
        guideList.add(ColorBarOptions(title = "title 2"))

        // should get the last in the list
        assertEquals("title 2", guideList.getTitle())

        guideList.add(GuideTitleOption("title 3"))
        guideList.add(LegendOptions(title = "title 4"))

        // should use GuideTitleOption if it is present
        assertEquals("title 3", guideList.getTitle())
    }


    @Test
    fun `scale() and guide() - legendOptions are combined`() {
        val scaleOptions = mapOf(
            Option.Scale.GUIDE to mapOf(
                Option.Meta.NAME to Option.Guide.LEGEND,
                Option.Guide.TITLE to "scale title",
                Option.Guide.Legend.ROW_COUNT to 2

            )
        )
        val guideOptions = mapOf(
            Option.Meta.NAME to Option.Guide.LEGEND,
            Option.Guide.TITLE to "guide title",
            Option.Guide.Legend.BY_ROW to false
        )
        val options = createGuideOptions(scaleOptions, guideOptions)

        val legendOptions = options.getLegendOptions()
        assertNotNull(legendOptions)
        assertEquals("guide title", legendOptions.title)
        assertEquals(2, legendOptions.rowCount)
        assertEquals(false, legendOptions.byRow)
    }

    @Test
    fun `scale() and guide() - colorbarOptions are not combined`() {
        val scaleOptions = mapOf(
            Option.Scale.GUIDE to mapOf(
                Option.Meta.NAME to Option.Guide.COLOR_BAR,
                Option.Guide.TITLE to "scale title",
                Option.Guide.ColorBar.HEIGHT to 10.0

            )
        )
        val guideOptions = mapOf(
            Option.Meta.NAME to Option.Guide.COLOR_BAR,
            Option.Guide.TITLE to "guide title",
            Option.Guide.ColorBar.WIDTH to 2.0
        )
        val options = createGuideOptions(scaleOptions, guideOptions)

        val colorBarOptions = options.getColorBarOptions()
        assertNotNull(colorBarOptions)
        assertEquals("guide title", colorBarOptions.title)
        assertEquals(null, colorBarOptions.height)
        assertEquals(2.0, colorBarOptions.width)
    }

    @Test
    fun `unnamed guide() should have a title`() {
        val guideOptions = mapOf(
            Option.Guide.Legend.BY_ROW to false
        )
        val exception = assertFailsWith(IllegalArgumentException::class) {
            createGuideOptions(emptyMap(), guideOptions)
        }
        assertEquals("Guide title is required", exception.message)
    }

    private fun createGuideOptions(
        scaleOptions: Map<String, Any>,
        guideOptionsMap: Map<String, Any>,
    ) : GuideOptionsList {
        val scaleConfig = ScaleConfig(
            aes = Aes.COLOR,
            options = scaleOptions,
            AesOptionConversion.demoAndTest
        )
        val guideOptions = PlotConfigFrontendUtil.createGuideOptions(
            listOf(scaleConfig),
            mapOf(Aes.COLOR.name to guideOptionsMap)
        )
        return guideOptions[Aes.COLOR.name]!!
    }
}