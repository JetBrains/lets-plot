/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import kotlin.test.Test
import kotlin.test.assertEquals

class WrapPathTest {

    private val domain = Rect.XYWH<Nothing>(0.0, 0.0, 256.0, 256.0)

    @Test
    fun horizontal() {
        assertEquals(
            expected = listOf(
                listOf(Vec(250, 128), Vec(256, 128)),
                listOf(Vec(0, 128), Vec(6, 128))
            ),
            actual = wrapPath(listOf(Vec(250, 128), Vec(6, 128)), domain)
        )

    }

    @Test
    fun horizontalReversed() {
        assertEquals(
            expected = listOf(
                listOf(Vec(6, 128), Vec(0, 128)),
                listOf(Vec(256, 128), Vec(250, 128)),
            ),
            actual = wrapPath(listOf(Vec(6, 128), Vec(250, 128)), domain)
        )
    }

    @Test
    fun diagonal() {
        assertEquals(
            expected = listOf(
                listOf(Vec(6, 150), Vec(0, 125)),
                listOf(Vec(256, 125), Vec(250, 100)),
            ),
            actual = wrapPath(listOf(Vec(6, 150), Vec(250, 100)), domain)
        )

    }

    @Test
    fun diagonalReversed() {
        assertEquals(
            expected = listOf(
                listOf(Vec(250, 100), Vec(256, 125)),
                listOf(Vec(0, 125), Vec(6, 150)),
            ),
            actual = wrapPath(listOf(Vec(250, 100), Vec(6, 150)), domain)
        )
    }

    @Test
    fun vertical() {
        assertEquals(
            expected = listOf(listOf(Vec(128, 100), Vec(128, 150))),
            actual = wrapPath(listOf(Vec(128, 100), Vec(128, 150)), domain)
        )
    }

    @Test
    fun verticalReversed() {
        assertEquals(
            expected = listOf(listOf(Vec(128, 100), Vec(128, 150))),
            actual = wrapPath(listOf(Vec(128, 100), Vec(128, 150)), domain)
        )
    }

    @Test
    fun realworldExample() {
        assertEquals(
            expected = listOf(
                listOf(Vec(x = 75.52021333333334, y = 26.027271983656874), Vec(x = 0.0, y = 92.17693483677954)),
                listOf(Vec(x = 256.0, y = 92.17693483677954), Vec(x = 216.1786311111111, y = 127.05726700325597))
            ),
            actual = wrapPath(
                listOf(
                    Vec(x = 75.52021333333334, y = 26.027271983656874),
                    Vec(x = 216.1786311111111, y = 127.05726700325597)
                ),
                domain
            )
        )
    }

    @Test
    fun realworldExampleReversed() {
        assertEquals(
            expected = listOf(
                listOf(Vec(x = 216.1786311111111, y = 127.05726700325597), Vec(x = 256.0, y = 92.17693483677954)),
                listOf(Vec(x = 0.0, y = 92.17693483677954), Vec(x = 75.52021333333334, y = 26.027271983656874))
            ),
            actual = wrapPath(
                listOf(
                    Vec(x = 216.1786311111111, y = 127.05726700325597),
                    Vec(x = 75.52021333333334, y = 26.027271983656874),
                ),
                domain
            )
        )
    }
}
