/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.mapping.svg.shared.SvgNodeSubtreeGeneratingSynchronizer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.raster.shape.Group

internal class SvgGElementMapper(
    source: SvgGElement,
    target: Group,
    peer: SvgCanvasPeer
) : SvgElementMapper<SvgGElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            val targetList = SvgUtils.elementChildren(target)
            conf.add(
                Synchronizers.forObservableRole(
                    this,
                    source.children(),
                    targetList,
                    SvgNodeMapperFactory(peer)
                )
            )
        } else {
//            UNSUPPORTED("isPrebuiltSubtree")
            conf.add(
                SvgNodeSubtreeGeneratingSynchronizer(
                    source,
                    target,
                    SkiaTargetPeer(peer)
                )
            )
        }
    }
}