/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import jetbrains.datalore.vis.StyleProperties
import jetbrains.datalore.vis.svg.SvgStyleElement

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgJfxPeer,
) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        // Parse CSS to prepare StyleProperties
        val styleProperties = StyleProperties.fromCSS(
            css = source.resource.css(),
            defaultFamily = "Helvetica",
            defaultSize = 15.0
        )
        peer.applyStyleProperties(styleProperties)
    }
}