/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.mapping.svg

import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.mapping.svg.shared.SvgNodeSubtreeGeneratingSynchronizer
import org.apache.batik.dom.AbstractDocument
import org.w3c.dom.Node

internal open class SvgNodeMapper<SourceT : SvgNode, TargetT : Node>(
    source: SourceT,
    target: TargetT,
    private val myDoc: AbstractDocument,
    private val myPeer: SvgBatikPeer
) : Mapper<SourceT, TargetT>(source, target) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            val target = Utils.elementChildren(target)
            conf.add(
                Synchronizers.forObservableRole(
                    this,
                    source.children(),
                    target,
                    SvgNodeMapperFactory(myDoc, myPeer)
                )
            )
        } else {
            conf.add(
                SvgNodeSubtreeGeneratingSynchronizer(
                    source,
                    target,
                    BatikTargetPeer(myDoc)
                )
            )
        }
    }

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        myPeer.registerMapper(source, this)
    }

    override fun onDetach() {
        super.onDetach()

        myPeer.unregisterMapper(source)
    }
}