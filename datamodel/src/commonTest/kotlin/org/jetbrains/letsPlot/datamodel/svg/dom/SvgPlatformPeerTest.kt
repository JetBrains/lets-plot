/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
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