/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import jetbrains.datalore.mapper.core.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import jetbrains.datalore.vis.svgMapper.SvgNodeSubtreeGeneratingSynchronizer

internal class SvgGElementMapper(
    source: SvgGElement,
    target: Group,
    peer: SvgJfxPeer
) : SvgElementMapper<SvgGElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            val targetList = Utils.elementChildren(target)
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
                    jetbrains.datalore.vis.svgMapper.jfx.JfxSceneTargetPeer()
                )
            )
        }
    }
}