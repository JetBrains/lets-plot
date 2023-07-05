/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.intern.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.gis.tileprotocol.mapConfig.MapConfig
import jetbrains.gis.tileprotocol.mapConfig.Rule
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.mapengine.basemap.BasemapLayerKind
import jetbrains.livemap.mapengine.viewport.CellKey

internal class TileDataRendererImpl(
    private val myMapConfigSupplier: () -> MapConfig?
) : TileDataRenderer {


    private fun getFeaturesByRule(
        zoom: Int,
        features: List<TileFeature>,
        rules: List<Rule>
    ): Map<Rule, List<TileFeature>> {
        val featuresByRule = HashMap<Rule, MutableList<TileFeature>>()

        for (feature in features) {
            for (rule in rules) {
                if (rule.predicate(feature, zoom)) {
                    featuresByRule.getOrPut(rule, ::ArrayList).add(feature)
                    break
                }
            }
        }
        return featuresByRule
    }

    override fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: BasemapLayerKind
    ): MicroTask<Async<Canvas.Snapshot>> {
        val ctx = canvas.context2d
        val size = canvas.size.toDoubleVector()
        val mapConfig = myMapConfigSupplier()

        val tasks = ArrayList<() -> Unit>()

        if (tileFeatures.isNotEmpty() && mapConfig != null) {
            tasks.add { ctx.setFillStyle(mapConfig.tileSheetBackgrounds[layerKind.toString()]) }
            tasks.add { ctx.fillRect(0.0, 0.0, size.x, size.y) }
            tasks.addAll(tileFeaturesDrawTasks(ctx, tileFeatures, layerKind, cellKey.length))
        } else {
            if (layerKind == BasemapLayerKind.WORLD) {
                tasks.add { ctx.drawDummyTile(size) }
            }
        }

        return MicroTaskUtil.create(tasks).map { canvas.takeSnapshot() }
    }

    private fun tileFeaturesDrawTasks(
        ctx: Context2d,
        tileFeatures: Map<String, List<TileFeature>>,
        layerKind: BasemapLayerKind,
        zoom: Int
    ): Collection<() -> Unit> {
        val mapConfig = myMapConfigSupplier() ?: return emptyList()

        val tasks = ArrayList<() -> Unit>()
        val labelBounds = ArrayList<DoubleRectangle>()


        for (layerName in mapConfig.getLayersByZoom(zoom)) {
            val rules = mapConfig.getLayerConfig(layerName).getRules(layerKind.toString()).flatten()
            val featuresByRule = getFeaturesByRule(zoom, tileFeatures[layerName]!!, rules)

            for (rule in rules) {
                tasks.add(ctx::save)

                val symbolizer = Symbolizer.create(rule.style, labelBounds)
                tasks.add { symbolizer.applyTo(ctx) }

                featuresByRule
                    .getOrElse(rule, ::emptyList)
                    .forEach { feature ->
                        symbolizer.createDrawTasks(ctx, feature).forEach { tasks.add(it) }
                    }

                tasks.add(ctx::restore)
            }
        }

        return tasks
    }

    private fun Context2d.drawDummyTile(size: DoubleVector) {
        save()
        setFillStyle(Color.GRAY)
        fillRect(0.0, 0.0, size.x, size.y)

        setStrokeStyle(Color.WHITE)
        strokeRect(0.0, 0.0, size.x, size.y)

        setStrokeStyle(Color.LIGHT_GRAY)
        moveTo(0.0, 0.0)
        lineTo(size.x, size.y)
        moveTo(0.0, size.y)
        lineTo(size.x, 0.0)
        stroke()
        restore()
    }
}