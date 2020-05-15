/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.Layer.MAP_JOIN
import jetbrains.datalore.plot.config.Option.Mapping.MAP_ID
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.Meta.MapJoin
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.getList
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector
import jetbrains.datalore.plot.config.write

class MapJoinChange: SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val (dataJoinColumn, mapJoinColumn) = spec.getList(MAP_JOIN)!!
        dataJoinColumn?.let { spec.write(MAPPING, MAP_ID) { it } }
        mapJoinColumn?.let { spec.write(MapJoin.MAP_JOIN_COLUMN) { it } }
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return spec.contains(MAP_JOIN)
                && spec.contains(MAP_DATA_META)
    }

    companion object {
        internal fun specSelector(): SpecSelector {
            return SpecSelector.of(Option.Plot.LAYERS)
        }
    }
}
