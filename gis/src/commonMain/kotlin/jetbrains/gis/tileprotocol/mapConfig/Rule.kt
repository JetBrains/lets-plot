package jetbrains.gis.tileprotocol.mapConfig

import jetbrains.gis.tileprotocol.TileFeature

class Rule(
    val minZoom: Int,
    val maxZoom: Int,
    val filters: List<(TileFeature) -> Boolean>,
    val style: Style
) {

    fun predicate(feature: TileFeature, zoom: Int): Boolean {
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
        var minZoom: Int? = null
        var maxZoom: Int? = null
        var filters: ArrayList<(TileFeature) -> Boolean> = ArrayList()
        lateinit var style: Style

        fun minZoom(minZoom: Int) { this.minZoom = minZoom }
        fun maxZoom(maxZoom: Int) { this.maxZoom = maxZoom }
        fun style(style: Style) { this.style = style }

        fun addFilterFunction(filter: (TileFeature) -> Boolean) {
            filters.add(filter)
        }

        fun build() = Rule(minZoom!!, maxZoom!!, filters, style)
    }
}