
import demo.svgMapping.model.ReferenceSvgModel
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@Ignore
class SvgTest {
    private val imageComparer = ImageComparer()

    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel(fontFamily = "/home/ikupriyanov/Projects/lets-plot/platf-imagick/src/nativeTest/resources/NotoSerif-Regular.ttf")

        imageComparer.assertImageEquals("svg_reference_test.bmp", svg)
    }
}
