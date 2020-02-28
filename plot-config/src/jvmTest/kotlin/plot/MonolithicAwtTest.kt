/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgUID
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


internal class MonolithicAwtTest {
    @Before
    fun setUp() {
        SvgUID.reset()
    }

    @Test
    fun svgFromSinglePlot() {
        val svgImages = MonolithicAwt.buildSvgImagesFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0),
            computationMessagesHandler = {
                throw AssertionError("Unexpected computation messages: $it")
            }
        )

//        println(svgImages[0])

        assertEquals(1, svgImages.size)
        assertEquals(EXPECTED_SINGLE_PLOT_SVG, svgImages[0])
    }

    @Test
    fun svgFromGGBunch() {
        val svgImages = MonolithicAwt.buildSvgImagesFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = null,
            computationMessagesHandler = {
                throw AssertionError("Unexpected computation messages: $it")
            }
        )

        assertEquals(2, svgImages.size)
        assertEquals(expectedSingleBunchItemSvg(0), svgImages[0])
        assertEquals(expectedSingleBunchItemSvg(1), svgImages[1])

//        println(svgImages[0])
    }
}