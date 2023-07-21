/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.mapConfig

interface TilePredicate {
    fun getFieldValue(key: String): Int
}

class Rule(
    val minZoom: Int,
    val maxZoom: Int,
    val filters: List<(TilePredicate) -> Boolean>,
    val style: Style
) {

    fun predicate(feature: TilePredicate, zoom: Int): Boolean {
        if (maxZoom < zoom || minZoom > zoom) {
            return false
        }

        for (f in filters) {
            if (!f(feature)) {
                return false
            }
        }
        return true
    }

    class RuleBuilder {
        private var minZoom: Int? = null
        private var maxZoom: Int? = null
        private var filters: ArrayList<(TilePredicate) -> Boolean> = ArrayList()
        private lateinit var style: Style

        fun minZoom(minZoom: Int) { this.minZoom = minZoom }
        fun maxZoom(maxZoom: Int) { this.maxZoom = maxZoom }
        fun style(style: Style) { this.style = style }

        fun addFilterFunction(filter: (TilePredicate) -> Boolean) {
            filters.add(filter)
        }

        fun build() = Rule(minZoom!!, maxZoom!!, filters, style)
    }
}