package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.values.Color

interface MapConfig {
    val tileSheetBackgrounds: Map<String, Color>
    fun getLayersByZoom(zoom: Int): List<String>
    fun getLayerConfig(layerName: String): LayerConfig

    interface LayerConfig {
        val name: String
        val border: Double
        val columns: List<String>
        val table: String
        val order: String?
        val tileSheets: Set<String>
        fun getRules(tileSheetName: String): List<List<Rule>>
    }

    interface Rule {
        val style: Style
        fun predicate(feature: TileFeature, zoom: Int): Boolean
    }

    interface Style {
        val type: String?
        val fill: Color?
        val stroke: Color?
        val strokeWidth: Double?
        val lineCap: String?
        val lineJoin: String?
        val lineDash: List<Double>?
        val labelField: String?
        val fontStyle: String?
        val fontface: String?
        val textTransform: String?
        val size: Double?
        val wrapWidth: Double?
        val minimumPadding: Double?
        val repeatDistance: Double?
        val shieldCornerRadius: Double?
        val shieldFillColor: Color?
        val shieldStrokeColor: Color?
    }
}
