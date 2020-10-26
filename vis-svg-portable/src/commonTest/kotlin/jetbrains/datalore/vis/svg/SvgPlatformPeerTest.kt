/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull

class SvgPlatformPeerTest {
    private var container: SvgNodeContainer? = null

    @BeforeTest
    fun setUp() {
        val root = SvgSvgElement()
        container = SvgNodeContainer(root)
    }

    @Test
    fun containerInit() {
        val c = container as SvgNodeContainer
        assertNull(c.getPeer())
    }
}