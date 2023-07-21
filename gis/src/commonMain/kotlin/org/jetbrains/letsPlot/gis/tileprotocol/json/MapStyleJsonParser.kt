/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.json


import org.jetbrains.letsPlot.commons.intern.json.FluentObject
import org.jetbrains.letsPlot.commons.intern.json.getAsInt
import org.jetbrains.letsPlot.commons.intern.json.getString
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.LayerConfig
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.MapConfig
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Rule
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Style

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

    fun parse(mapStyle: MutableMap<String, Any?>): MapConfig {
        val mapStyleJson = FluentObject(mapStyle)
        val colors = readColors(mapStyleJson.getObject(COLORS))
        val styles = readStyles(mapStyleJson.getObject(STYLES), colors)

        return MapConfig.MapConfigBuilder()
            .tileSheetBackgrounds(readTileSheets(mapStyleJson.getObject(TILE_SHEETS), colors))
            .colors(colors)
            .layerNamesByZoom(readZooms(mapStyleJson.getObject(ZOOMS)))
            .layers(readLayers(mapStyleJson.getObject(LAYERS), styles))
            .build()
    }

    private fun readStyles(stylesJson: FluentObject, colors: Map<String, Color>): Map<String, List<Rule>> {
        val styles = HashMap<String, List<Rule>>()

        stylesJson.forArrEntries { styleName, rules -> styles[styleName] =
            rules.map { readRule(FluentObject(it as Map<*, *>), colors) }
        }

        return styles
    }

    private fun readZooms(zooms: FluentObject): Map<Int, List<String>> {
        val zoomMap = HashMap<Int, List<String>>()

        for (zoom in MIN_ZOOM..MAX_ZOOM) {
            zoomMap[zoom] = zooms.getArray(zoom.toString()).stream().map { it as String }.toList()
        }

        return zoomMap
    }

    private fun readLayers(layers: FluentObject, styles: Map<String, List<Rule>>): Map<String, LayerConfig> {
        val layerConfigMap = HashMap<String, LayerConfig>()
        layers.forObjEntries{name, layer ->
            layerConfigMap[name] = parseLayerConfig(name, FluentObject(layer), styles)
        }
        return layerConfigMap
    }

    private fun readColors(colorsJson: FluentObject): Map<String, Color> {
        val colors = HashMap<String, Color>()

        colorsJson.forEntries {
                colorName, colorString -> colors[colorName] = parseHexWithAlpha(colorString as String)
        }

        return colors
    }

    private fun readTileSheets(tiles: FluentObject, colors: Map<String, Color>): Map<String, Color> {
        val sheetBackgrounds = HashMap<String, Color>()

        tiles.forObjEntries { sheetName, tile ->
            sheetBackgrounds[sheetName] = colors.getValue(tile.getString(BACKGROUND))
        }

        return sheetBackgrounds
    }

    private fun readRule(ruleJson: FluentObject, colors: Map<String, Color>): Rule {
        val builder = Rule.RuleBuilder()

        ruleJson
            .getIntOrDefault(MIN_ZOOM_FIELD, builder::minZoom, MIN_ZOOM)
            .getIntOrDefault(MAX_ZOOM_FIELD, builder::maxZoom, MAX_ZOOM)
            .getExistingObject(FILTER) { filter ->
                filter.forEntries { key, value ->
                    val predicate: (Int) -> Boolean
                    predicate = when (value) {
                        is List<*> -> {
                            val filterValues: List<Int> = value.map(::getAsInt)
                            ({ filterValues.contains(it)  })
                        }
                        is Number -> {
                            val intValue = getAsInt(value)
                            ({ v -> v == intValue })
                        }
                        is Map<*, *> -> { // is JsonObject
                            makeCompareFunctions(FluentObject(value))
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
                    .getExistingString(FONT_FACE) { style.fontFamily = it }
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

    private fun makeCompareFunctions(condition: FluentObject): (Int) -> Boolean {
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

    private fun parseLayerConfig(layerName: String, layerJson: FluentObject, styles: Map<String, List<Rule>>): LayerConfig {
        val layerConfig = LayerConfig()

        layerConfig.name = layerName
        layerJson
            .getDouble(BORDER) { layerConfig.border = it }
            .getString(TABLE) { layerConfig.table = it }
            .getExistingString(ORDER) { layerConfig.order = it }
            .getStrings(COLUMNS) { layerConfig.columns = it.requireNoNulls() }
            .getObject(TILE_SHEETS) { tileSheetsJson ->
                val rulesByTileSheet = HashMap<String, List<List<Rule>>>()

                tileSheetsJson
                    .forArrEntries { tileSheets, styleNames ->
                        rulesByTileSheet[tileSheets] = styleNames.map { styles.getValue(it as String) }
                    }

            layerConfig.rulesByTileSheet = rulesByTileSheet
        }

        return layerConfig
    }

    private fun parseHexWithAlpha(colorString: String) =
        Color
            .parseHex(colorString.substring(0, 7))
            .changeAlpha(colorString.substring(7, 9).toInt(16))
}

