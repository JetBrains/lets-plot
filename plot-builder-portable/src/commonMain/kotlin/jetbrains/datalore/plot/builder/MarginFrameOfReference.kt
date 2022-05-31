/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.vis.svg.SvgRectElement

internal class MarginFrameOfReference(
    private val bounds: DoubleRectangle
) : FrameOfReference {
    override fun drawBeforeGeomLayer(parent: SvgComponent) {}

    override fun drawAfterGeomLayer(parent: SvgComponent) {}

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        return object : SvgComponent() {
            override fun buildComponent() {
                rootGroup.children().add(
                    SvgRectElement(bounds).apply {
                        strokeColor().set(Color.ORANGE)
                        fillColor().set(Color.ORANGE)
                        strokeWidth().set(0.0)
                        fillOpacity().set(0.3)
                    }
                )
            }
        }

//        layerComponent.moveTo(geomOuterBounds.origin.add(origin))
//        return layerComponent
    }
}