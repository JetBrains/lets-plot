/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.interact.*

class LayerTargetCollectorWithLocator(
    private val geomKind: GeomKind,
    private val lookupSpec: GeomTargetLocator.LookupSpec,
    private val contextualMapping: ContextualMapping
) : GeomTargetCollector, GeomTargetLocator {

    private val myTargets = ArrayList<TargetPrototype>()
    private var myLocator: GeomTargetLocator? = null

    override fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        if (contextualMapping.ignoreInvisibleTargets) {
            if (radius == 0.0 || tooltipParams.markerColorsFactory(index).all { it.alpha == 0 }) {
                return
            }
        }
        addTarget(
            TargetPrototype(
                HitShape.point(point, radius),
                { index },
                tooltipParams,
                tooltipKind
            )
        )
    }

    override fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        if (contextualMapping.ignoreInvisibleTargets) {
            if (rectangle.width == 0.0 || rectangle.height == 0.0 || tooltipParams.markerColorsFactory(index).all { it.alpha == 0 }) {
                return
            }
        }
        addTarget(
            TargetPrototype(
                HitShape.rect(rectangle),
                { index },
                tooltipParams,
                tooltipKind
            )
        )
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        addTarget(
            TargetPrototype(
                HitShape.path(points),
                localToGlobalIndex,
                tooltipParams,
                tooltipKind
            )
        )
    }

    override fun addPolygon(
        points: List<DoubleVector>,
        index: Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        addTarget(
            TargetPrototype(
                HitShape.polygon(points),
                { index },
                tooltipParams,
                tooltipKind
            )
        )
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        return FlippedTargetCollector(this)
    }

    override fun withYOrientation(): GeomTargetCollector {
        return YOrientationTargetCollector(this)
    }

    private fun addTarget(targetPrototype: TargetPrototype) {
        myTargets.add(targetPrototype)
        myLocator = null
    }

    override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
        if (myLocator == null) {
            myLocator = LayerTargetLocator(
                geomKind,
                lookupSpec,
                contextualMapping,
                myTargets
            )
        }
        return myLocator!!.search(coord)
    }
}
