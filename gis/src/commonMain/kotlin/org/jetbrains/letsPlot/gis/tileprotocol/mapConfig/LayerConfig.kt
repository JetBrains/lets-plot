/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.mapConfig

class LayerConfig {
    lateinit var name: String
    var border: Double = 0.0
    lateinit var columns: List<String>
    lateinit var table: String
    lateinit var rulesByTileSheet: Map<String, List<List<Rule>>>
    var order: String? = null

    fun tileSheets(): Set<String> {
        return rulesByTileSheet.keys
    }

    fun getRules(tileSheetName: String): List<List<Rule>> {
        return rulesByTileSheet.getOrElse(tileSheetName, { emptyList() })
    }
}