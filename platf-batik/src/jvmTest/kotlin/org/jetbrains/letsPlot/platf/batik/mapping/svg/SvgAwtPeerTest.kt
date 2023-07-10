/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.batik.mapping.svg

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SvgAwtPeerTest {
    private val root = SvgSvgElement()
    private val container: SvgNodeContainer =
        SvgNodeContainer(root)
    private val mapper: SvgRootDocumentMapper =
        SvgRootDocumentMapper(root)

    @Test
    fun documentInitNullPeer() {
        assertNull(container.getPeer())
    }

    @Test
    fun mapperAttachPeerSet() {
        mapper.attachRoot()
        assertNotNull(container.getPeer())
    }

    @Test
    fun mapperDetachRootNullPeer() {
        mapper.attachRoot()
        mapper.detachRoot()
        assertNull(container.getPeer())
    }
}
