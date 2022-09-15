/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.vis.StyleSheet
import jetbrains.datalore.vis.svg.SvgStyleElement

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgJfxPeer,
) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        val styleSheet = StyleSheet.fromCSS(
            css = source.resource.css(),
            defaultFamily = FontFamily.HELVETICA.name,
            defaultSize = 15.0
        )
        peer.applyStyleSheet(styleSheet)
    }
}