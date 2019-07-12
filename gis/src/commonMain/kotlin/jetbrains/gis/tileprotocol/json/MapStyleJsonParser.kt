package jetbrains.gis.tileprotocol.json


import jetbrains.datalore.base.values.Color
import jetbrains.gis.common.json.*
import jetbrains.gis.tileprotocol.mapConfig.LayerConfig
import jetbrains.gis.tileprotocol.mapConfig.MapConfig
import jetbrains.gis.tileprotocol.mapConfig.Rule
import jetbrains.gis.tileprotocol.mapConfig.Style

object MapStyleJsonParser {
    private const val MIN_ZOOM_FIELD = "minZoom"
    private const val MAX_ZOOM_FIELD = "maxZoom"
    private const val ZOOMS = "zooms"

    private const val LAYERS = "layers"
    private const val BORDER = "border"
    private const val TABLE = "table"
    private const val COLUMNS = "columns"
    private const val ORDER = "order"
    private const val COLORS = "colors"
    private const val STYLES = "styles"

    private const val TILE_SHEETS = "tiles"
    private const val BACKGROUND = "background"

    private const val FILTER = "filter"
    private const val GT = "\$gt"
    private const val GTE = "\$gte"
    private const val LT = "\$lt"
    private const val LTE = "\$lte"

    private const val SYMBOLIZER = "symbolizer"
    private const val TYPE = "type"
    private const val FILL = "fill"
    private const val STROKE = "stroke"
    private const val STROKE_WIDTH = "stroke-width"
    private const val LINE_CAP = "stroke-linecap"
    private const val LINE_JOIN = "stroke-linejoin"
    private const val LABEL_FIELD = "label"
    private const val FONT_STYLE = "fontStyle"
    private const val FONT_FACE = "fontface"
    private const val TEXT_TRANSFORM = "text-transform"
    private const val SIZE = "size"
    private const val WRAP_WIDTH = "wrap-width"
    private const val MINIMUM_PADDING = "minimum-padding"
    private const val REPEAT_DISTANCE = "repeat-distance"
    private const val SHIELD_CORNER_RADIUS = "shield-corner-radius"
    private const val SHIELD_FILL_COLOR = "shield-fill-color"
    private const val SHIELD_STROKE_COLOR = "shield-stroke-color"

    private const val MIN_ZOOM = 1
    private const val MAX_ZOOM = 15

    fun parse(mapStyle: JsonObject): MapConfig {
        val colors = readColors(mapStyle.getObject(COLORS))
        val styles = readStyles(mapStyle.getObject(STYLES), colors)

        return MapConfig.MapConfigBuilder()
            .tileSheetBackgrounds(readTileSheets(mapStyle.getObject(TILE_SHEETS), colors))
            .colors(colors)
            .layerNamesByZoom(readZooms(mapStyle.getObject(ZOOMS)))
            .layers(readLayers(mapStyle.getObject(LAYERS), styles))
            .build()
    }

    private fun readStyles(stylesJson: JsonObject, colors: Map<String, Color>): Map<String, List<Rule>> {
        val styles = HashMap<String, List<Rule>>()

        for (styleName in stylesJson.keys) {
            val rulesList = ArrayList<Rule>()

            for (ruleJson in stylesJson.getArray(styleName)) {
                rulesList.add(readRule(ruleJson as JsonObject, colors))
            }

            styles[styleName] = rulesList
        }

        return styles
    }

    private fun readZooms(zooms: JsonObject): Map<Int, List<String>> {
        val zoomMap = HashMap<Int, List<String>>()

        for (zoom in MIN_ZOOM..MAX_ZOOM) {
            val layers = ArrayList<String>()
            zooms.getArray(zoom.toString())
                .forEach { layerName -> layers.add(layerName as String) }

            zoomMap[zoom] = layers
        }

        return zoomMap
    }

    private fun readLayers(layers: JsonObject, styles: Map<String, List<Rule>>): Map<String, LayerConfig> {
        val layerConfigMap = HashMap<String, LayerConfig>()
        layers.keys.forEach { name -> layerConfigMap[name] = parseLayerConfig(name, layers.getObject(name), styles) }
        return layerConfigMap
    }

    private fun readColors(colorsJson: JsonObject): Map<String, Color> {
        val colors = HashMap<String, Color>()

        for (colorName in colorsJson.keys) {
            colors.put(colorName, parseHexWithAlpha(colorsJson.getString(colorName)))
        }

        return colors
    }

    private fun readTileSheets(tiles: JsonObject, colors: Map<String, Color>): Map<String, Color> {
        val sheetBackgrounds = HashMap<String, Color>()

        for (sheetName in tiles.keys) {
            sheetBackgrounds[sheetName] = colors.getValue(tiles.getObject(sheetName).getString(BACKGROUND))
        }

        return sheetBackgrounds
    }

    private fun readRule(json: JsonObject, colors: Map<String, Color>): Rule {
        val ruleJson = FluentJsonObject(json)

        val builder = Rule.RuleBuilder()

        ruleJson
            .getIntOrDefault(MIN_ZOOM_FIELD, builder::minZoom, MIN_ZOOM)
            .getIntOrDefault(MAX_ZOOM_FIELD, builder::maxZoom, MAX_ZOOM)
            .getExistingObject(FILTER) { filter ->
                filter.forEntries { key, value ->
                    val predicate: (Int) -> Boolean
                    when (value) {
                        is JsonArray -> {
                            val filterValues: List<Int> =
                                FluentJsonArray(value)
                                    .stream()
                                    .map { JsonUtils.getAsInt(it) }

                            predicate = { filterValues.contains(it)  }
                        }
                        is Number -> {
                            val intValue = JsonUtils.getAsInt(value)
                            predicate = { v -> v == intValue }
                        }
                        is HashMap<*, *> -> { // is JsonObject
                            predicate = makeCompareFunctions(FluentJsonObject(value as JsonObject))
                        }
                        else -> throw IllegalStateException("Unsupported filter type.")
                    }

                    builder.addFilterFunction { feature -> predicate(feature.getFieldValue(key)) }
                }
            }.getExistingObject(SYMBOLIZER) { symbolizerJson ->
                val style = Style()

                symbolizerJson
                    .getExistingString(TYPE) { style.type = it }
                    .getExistingString(FILL) { style.fill = colors[it] }
                    .getExistingString(STROKE) { style.stroke = colors[it] }
                    .getExistingDouble(STROKE_WIDTH) { style.strokeWidth = it }
                    .getExistingString(LINE_CAP) { style.lineCap = it }
                    .getExistingString(LINE_JOIN) { style.lineJoin = it }
                    .getExistingString(LABEL_FIELD) { style.labelField = it }
                    .getExistingString(FONT_STYLE) { style.fontStyle = it }
                    .getExistingString(FONT_FACE) { style.fontface = it }
                    .getExistingString(TEXT_TRANSFORM) { style.textTransform = it }
                    .getExistingDouble(SIZE) { style.size = it }
                    .getExistingDouble(WRAP_WIDTH) { style.wrapWidth = it }
                    .getExistingDouble(MINIMUM_PADDING) { style.minimumPadding = it }
                    .getExistingDouble(REPEAT_DISTANCE) { style.repeatDistance = it }
                    .getExistingDouble(SHIELD_CORNER_RADIUS) { style.shieldCornerRadius = it }
                    .getExistingString(SHIELD_FILL_COLOR) { style.shieldFillColor = colors[it] }
                    .getExistingString(SHIELD_STROKE_COLOR) {style.shieldStrokeColor = colors[it] }

                builder.style(style)
            }

        return builder.build()
    }

    private fun makeCompareFunctions(condition: FluentJsonObject): (Int) -> Boolean {
        val conditionValue: Int
        when {
            condition.contains(GT) -> {
                conditionValue = condition.getInt(GT)
                return { v -> v > conditionValue }
            }
            condition.contains(GTE) -> {
                conditionValue = condition.getInt(GTE)
                return { v -> v >= conditionValue }
            }
            condition.contains(LT) -> {
                conditionValue = condition.getInt(LT)
                return { v -> v < conditionValue }
            }
            condition.contains(LTE) -> {
                conditionValue = condition.getInt(LTE)
                return { v -> v <= conditionValue }
            }
            else -> throw IllegalStateException("Unknown condition type")
        }

    }

    private fun parseLayerConfig(layerName: String, json: JsonObject, styles: Map<String, List<Rule>>): LayerConfig {
        val layerJson = FluentJsonObject(json)
        val layerConfig = LayerConfig()

        layerConfig.name = layerName
        layerJson
            .getDouble(BORDER) { layerConfig.border = it }
            .getString(TABLE) { layerConfig.table = it }
            .getExistingString(ORDER) { layerConfig.order = it }
            .getStrings(COLUMNS) { layerConfig.columns = it as List<String> }
            .getObject(TILE_SHEETS) { tileSheetsJson ->
                val rulesByTileSheet = HashMap<String, List<List<Rule>>>()

                tileSheetsJson
                    .forEntries { tileSheets, styleNames ->
                        val list = ArrayList<List<Rule>>()

                        FluentJsonArray(styleNames as JsonArray).stream()
                            .forEach { name -> list.add(styles.getValue(name as String)) }

                        rulesByTileSheet[tileSheets] = list
                    }

            layerConfig.rulesByTileSheet = rulesByTileSheet
        }

        return layerConfig
    }

    private fun parseHexWithAlpha(colorString: String): Color {


        return Color
            .parseHex(colorString.substring(0, 7))
            .changeAlpha(colorString.substring(7, 9).toInt(16))
    }
}

