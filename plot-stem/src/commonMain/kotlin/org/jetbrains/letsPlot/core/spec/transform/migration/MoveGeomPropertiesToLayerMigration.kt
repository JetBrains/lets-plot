/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform.migration

import org.jetbrains.letsPlot.core.spec.Option.Layer.GEOM
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.transform.PlotSpecTransformUtil
import org.jetbrains.letsPlot.core.spec.transform.SpecChange
import org.jetbrains.letsPlot.core.spec.transform.SpecChangeContext
import org.jetbrains.letsPlot.core.spec.transform.SpecSelector

/**
 * Migrate from old schema:
 * <pre>
 * layers:  [
 * {
 * geom: {
 * name: 'point'
 * data: ....
 * }
 * },
 * ...
 * ]
</pre> *
 *
 *
 * to new schema:
 *
 *
 * <pre>
 * layers:  [
 * {
 * geom: 'point'
 * data: ....
 * },
 * ...
 * ]
</pre> *
 *
 *
 *
 *
 * ToDo: add test
 */
class MoveGeomPropertiesToLayerMigration : SpecChange {

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        // migrate if value of 'geom' property is Map-object
        return spec[GEOM] is Map<*, *>
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val geomSpec = spec.remove(GEOM) as MutableMap<*, *>
        val name = geomSpec.remove(Meta.NAME) as String

        spec[GEOM] = name
        @Suppress("UNCHECKED_CAST")
        spec.putAll(geomSpec as Map<String, Any>)
    }

    companion object {
        fun specSelector(isGGBunch: Boolean): SpecSelector {
            val parts = ArrayList<String>()
            if (isGGBunch) {
                parts.addAll(PlotSpecTransformUtil.GGBUNCH_KEY_PARTS.toList())
            }
            parts.add(Plot.LAYERS)
            return SpecSelector.from(parts)  // apply to each element in 'layers' list (i.e. to layer spec)
        }
    }
}
