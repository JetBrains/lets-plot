/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgStyleElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle.Companion.NONE_FAMILY
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle.Companion.NONE_SIZE
import org.jetbrains.letsPlot.raster.shape.Group

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgCanvasPeer,
) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        val styleSheet = StyleSheet.fromCSS(
            css = source.resource.css(),
            defaultFamily = NONE_FAMILY,
            defaultSize = NONE_SIZE
        )
        peer.applyStyleSheet(styleSheet)
    }
}