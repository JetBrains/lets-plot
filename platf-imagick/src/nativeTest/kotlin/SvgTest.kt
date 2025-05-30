
import demo.svgMapping.model.ReferenceSvgModel
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@Ignore
class SvgTest {
    private val outDir: String = getCurrentDir() + "/build/image-test/"
    private val expectedDir: String = getCurrentDir() + "/src/nativeTest/resources/expected/"

    init {
        mkDir(outDir)
    }

    private val imageComparer = ImageComparer(
        expectedDir = expectedDir,
        outDir = outDir
    )

    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel(fontFamily = "/home/ikupriyanov/Projects/lets-plot/platf-imagick/src/nativeTest/resources/NotoSerif-Regular.ttf")

        imageComparer.assertImageEquals("svg_reference_test.bmp", svg)
    }
}