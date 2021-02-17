/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.*

class LayerTargetCollectorWithLocator(
    private val geomKind: GeomKind,
    private val lookupSpec: GeomTargetLocator.LookupSpec,
    private val contextualMapping: ContextualMapping,
    private val coordinateSystem: CoordinateSystem
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
            if (radius == 0.0 || tooltipParams.getColor().alpha == 0) {
                return;
            }
        }
        if (!coordinateSystem.isPointInLimits(point)) {
            return
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
            if (rectangle.width == 0.0 || rectangle.height == 0.0 || tooltipParams.getColor().alpha == 0) {
                return
            }
        }
        if (!coordinateSystem.isRectInLimits(rectangle)) {
            return
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
        if (!coordinateSystem.isPathInLimits(points)) {
            return
        }
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
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        if (!coordinateSystem.isPolygonInLimits(points)) {
            return
        }
        addTarget(
            TargetPrototype(
                HitShape.polygon(points),
                localToGlobalIndex,
                tooltipParams,
                tooltipKind
            )
        )
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
