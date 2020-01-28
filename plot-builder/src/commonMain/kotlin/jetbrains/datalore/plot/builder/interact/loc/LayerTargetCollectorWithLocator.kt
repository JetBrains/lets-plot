/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.HitShape

class LayerTargetCollectorWithLocator(
    private val geomKind: GeomKind,
    private val lookupSpec: GeomTargetLocator.LookupSpec,
    private val contextualMapping: ContextualMapping
) : GeomTargetCollector, GeomTargetLocator {

    private val myTargets = ArrayList<TargetPrototype>()
    private var myLocator: GeomTargetLocator? = null

    override fun addPoint(index: Int, point: DoubleVector, radius: Double, tooltipParams: GeomTargetCollector.TooltipParams) {
        addTarget(
            TargetPrototype(
                HitShape.point(point, radius),
                { index },
                tooltipParams
            )
        )
    }

    override fun addRectangle(index: Int, rectangle: DoubleRectangle, tooltipParams: GeomTargetCollector.TooltipParams) {
        addTarget(
            TargetPrototype(
                HitShape.rect(rectangle),
                { index },
                tooltipParams
            )
        )
    }

    override fun addPath(points: List<DoubleVector>, localToGlobalIndex: (Int) -> Int, tooltipParams: GeomTargetCollector.TooltipParams, closePath: Boolean) {
        addTarget(
            TargetPrototype(
                HitShape.path(points, closePath),
                localToGlobalIndex,
                tooltipParams
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
