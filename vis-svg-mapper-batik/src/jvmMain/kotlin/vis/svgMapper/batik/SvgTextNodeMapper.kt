/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.batik

import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.mapper.core.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.apache.batik.dom.AbstractDocument
import org.w3c.dom.Text

internal class SvgTextNodeMapper(source: SvgTextNode, target: Text, doc: AbstractDocument, peer: SvgBatikPeer) : SvgNodeMapper<SvgTextNode, Text>(source, target, doc, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(Synchronizers.forPropsOneWay(source.textContent(), object : WritableProperty<String?> {
            override fun set(value: String?) {
                target.nodeValue = value
            }
        }))
    }
}