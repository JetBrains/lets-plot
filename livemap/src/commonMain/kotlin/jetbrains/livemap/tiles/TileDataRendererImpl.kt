package jetbrains.livemap.tiles

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.gis.tileprotocol.mapConfig.MapConfig
import jetbrains.gis.tileprotocol.mapConfig.Rule
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.projections.CellKey

internal class TileDataRendererImpl(
    private val myCanvasSupplier: () -> Canvas,
    private val myMapConfigSupplier: () -> MapConfig?
) : TileDataRenderer {
    private fun getFeaturesByRule(
        zoom: Int,
        features: List<TileFeature>,
        rulesGroupedByStyles: List<List<Rule>>
    ): Map<Rule, List<TileFeature>> {
        val featuresByRule = HashMap<Rule, MutableList<TileFeature>>()

        for (feature in features) {
            for (rules in rulesGroupedByStyles) {
                for (rule in rules) {
                    if (rule.predicate(feature, zoom)) {
                        featuresByRule.getOrPut(rule, ::ArrayList).add(feature)
                        break
                    }
                }
            }
        }
        return featuresByRule
    }

    override fun render(
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: Components.CellLayerKind
    ): MicroTask<Async<Canvas.Snapshot>> {
        val zoom = cellKey.toString().length
        val canvas = myCanvasSupplier()
        val ctx = canvas.context2d
        val size = canvas.size
        val mapConfig = myMapConfigSupplier()

        val tasks = ArrayList<() -> Unit>()

        if (tileFeatures.isNotEmpty() && mapConfig != null) {
            tasks.add { ctx.setFillStyle(mapConfig.tileSheetBackgrounds[layerKind.toString()]!!.toCssColor()) }
            tasks.add { ctx.fillRect(0.0, 0.0, size.x.toDouble(), size.y.toDouble()) }

            val labelBounds = ArrayList<DoubleRectangle>()

            for (layerName in mapConfig.getLayersByZoom(zoom)) {
                val layerConfig = mapConfig.getLayerConfig(layerName)

                val rulesGroupedByStyles = layerConfig.getRules(layerKind.toString())
                val featuresByRule = getFeaturesByRule(zoom, tileFeatures[layerName]!!, rulesGroupedByStyles)

                for (rules in rulesGroupedByStyles) {
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
            }
        } else {
            if (layerKind === Components.CellLayerKind.WORLD) {
                tasks.add {
                    ctx.save()
                    ctx.setFillStyle(Color.GRAY.toCssColor())
                    ctx.fillRect(0.0, 0.0, size.x.toDouble(), size.y.toDouble())

                    ctx.setStrokeStyle(Color.WHITE.toCssColor())
                    ctx.strokeRect(0.0, 0.0, size.x.toDouble(), size.y.toDouble())

                    ctx.setStrokeStyle(Color.LIGHT_GRAY.toCssColor())
                    ctx.moveTo(0.0, 0.0)
                    ctx.lineTo(size.x.toDouble(), size.y.toDouble())
                    ctx.moveTo(0.0, size.y.toDouble())
                    ctx.lineTo(size.x.toDouble(), 0.0)
                    ctx.stroke()
                    ctx.restore()
                }
            }
        }

        return MicroTaskUtil.create(tasks).map { canvas.takeSnapshot() }
    }
}