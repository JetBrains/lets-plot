/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.core.canvas.CanvasDrawable
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.GenericLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAttributeSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgStylableElement


class LiveMapGeom : Geom {
    private var liveMapProvider: LiveMapProvider? = null
    private var liveMapData: LiveMapProvider.LiveMapData? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = GenericLegendKeyElementFactory()

    override fun build(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val livemapCanvasFigure = liveMapData?.canvasDrawable as? CanvasDrawable ?: error("LiveMap data missing")
        root.add(SvgCanvasDrawableElement(livemapCanvasFigure))
    }

    fun setLiveMapProvider(liveMapProvider: LiveMapProvider) {
        this.liveMapProvider = liveMapProvider
    }

    fun createCanvasFigure(bounds: DoubleRectangle): LiveMapProvider.LiveMapData? {
        val liveMapProvider = liveMapProvider ?: error("LiveMapProvider not initialized")
        liveMapData = liveMapProvider.createLiveMap(bounds)
        return liveMapData
    }

    companion object {
        const val HANDLES_GROUPS = false
    }

    class SvgCanvasDrawableElement(content: CanvasDrawable) : SvgStylableElement() {
        companion object {
            val CONTENT: SvgAttributeSpec<CanvasDrawable> =
                SvgAttributeSpec.createSpec("content")
        }

        init {
            content().set(content)
        }

        override val elementName: String = "canvasfigure"

        fun content(): Property<CanvasDrawable?> {
            return getAttribute(CONTENT)
        }
    }

}
