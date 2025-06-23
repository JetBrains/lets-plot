
import demo.svgMapping.model.ReferenceSvgModel
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

//@Ignore
class SvgTest {
    private val imageComparer = ImageComparer(suffix = getOSName())

    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel()

        imageComparer.assertImageEquals("svg_reference_test.bmp", svg)
    }
}
