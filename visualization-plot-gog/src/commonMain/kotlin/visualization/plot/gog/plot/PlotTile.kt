package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.base.canvasFigure.SvgCanvasFigure
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.core.event3.MouseEventSource
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem
import jetbrains.datalore.visualization.plot.gog.core.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.gog.core.scale.Mappers
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.plot.assemble.GeomContextBuilder
import jetbrains.datalore.visualization.plot.gog.plot.event3.GeomTargetController
import jetbrains.datalore.visualization.plot.gog.plot.event3.TileMouseEventPeer
import jetbrains.datalore.visualization.plot.gog.plot.guide.AxisComponent
import jetbrains.datalore.visualization.plot.gog.plot.layout.AxisLayoutInfo
import jetbrains.datalore.visualization.plot.gog.plot.layout.GeometryUtil.round
import jetbrains.datalore.visualization.plot.gog.plot.layout.TileLayoutInfo
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

internal class PlotTile(layers: List<GeomLayer>,
                        private val myScaleX: Scale2<Double>, private val myScaleY: Scale2<Double>,
                        private val myTilesOrigin: DoubleVector, private val myLayoutInfo: TileLayoutInfo, private val myCoord: CoordinateSystem, private val myTheme: Theme,
                        private val myMouseEventSource: MouseEventSource) : SvgComponent() {

    private val myDebugDrawing = ValueProperty(false)

    private val myLayers: List<GeomLayer>
    private val myCanvasFigures = ArrayList<CanvasFigure>()
    private val myTargetLocators = ArrayList<GeomTargetLocator>()

    private var myShowAxis: Boolean = false
    private var myUseCanvas: Boolean = false

    val canvasFigures: List<CanvasFigure>
        get() = myCanvasFigures

    val targetLocators: List<GeomTargetLocator>
        get() = myTargetLocators

    private val isDebugDrawing: Boolean
        get() = myDebugDrawing.get()

    init {
        myLayers = ArrayList(layers)

        moveTo(myLayoutInfo.getAbsoluteBounds(myTilesOrigin).origin)
    }

    override fun buildComponent() {
        /*
    // Don't set this flag: it was harmless when we were using SvgNodeSubtreeGeneratingSynchronizer but with new
    // SvgNodeSubtreeBufferGeneratingSynchronizer this leads to having all svg event handlers ignored
    // because the entire plot panel will be generated to a string buffer.
    // We want event handlers to be called on SvgElement-s
    getRootGroup().setPrebuiltSubtree(true);
    */
        val geomBounds = myLayoutInfo.geomBounds
        addFacetLabels(geomBounds)

        val isLivemap = GeomLayerListUtil.containsLivemapLayer2(myLayers)
        if (!isLivemap && myShowAxis) {
            addAxis(geomBounds)
        }

        if (isDebugDrawing) {
            val tileBounds = myLayoutInfo.bounds
            val rect = SvgRectElement(tileBounds)
            rect.fillColor().set(Color.BLACK)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.1)
            add(rect)
        }

        if (isDebugDrawing) {
            val clipBounds = myLayoutInfo.clipBounds
            val rect = SvgRectElement(clipBounds)
            rect.fillColor().set(Color.DARK_GREEN)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.3)
            add(rect)
        }

        if (isDebugDrawing) {
            val rect = SvgRectElement(geomBounds)
            rect.fillColor().set(Color.PINK)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.5)
            add(rect)
        }

        // render geoms

        if (isLivemap) {
            // 'live map' requires all positions to be passed "as is", without mapping
            val livemapLayer = GeomLayerListUtil.getLivemapLayer(myLayers)
            val origin = myLayoutInfo.getAbsoluteGeomBounds(myTilesOrigin).origin
            val livemapLayerRenderer = LayerRendererUtil.createLivemapLayerRenderer(livemapLayer, myLayers)

            val rectElement = SvgRectElement(geomBounds)
            rectElement.addClass(Style.PLOT_GLASS_PANE)
            rectElement.opacity().set(0.0)
            add(rectElement)

            val rect = Rectangle(round(origin), round(geomBounds.dimension))

            val livemapData = livemapLayerRenderer.createLivemapData(
                    DoubleRectangle(origin, geomBounds.dimension),
                    TileMouseEventPeer(myMouseEventSource, rect)
            )
            myCanvasFigures.add(livemapData.canvasFigure)
            myTargetLocators.add(livemapData.targetLocator)

        } else {
            // normal plot tile
            val sharedNumericMappers = HashMap<Aes<Double>, (Double?) -> Double?>()
            val overallNumericDomains = HashMap<Aes<Double>, ClosedRange<Double>>()

            val xAxisInfo = myLayoutInfo.xAxisInfo
            val yAxisInfo = myLayoutInfo.yAxisInfo
            val mapperX = myScaleX.mapper
            val mapperY = myScaleY.mapper

            sharedNumericMappers[Aes.X] = mapperX
            sharedNumericMappers[Aes.Y] = mapperY
            sharedNumericMappers[Aes.SLOPE] = Mappers.mul(mapperY(1.0)!! / mapperX(1.0)!!)

            overallNumericDomains[Aes.X] = xAxisInfo!!.axisDomain!!
            overallNumericDomains[Aes.Y] = yAxisInfo!!.axisDomain!!

            val geomLayerRenderers = buildGeoms(sharedNumericMappers, overallNumericDomains, myCoord)
            for (layerRenderer in geomLayerRenderers) {
                val layerComponent = layerRenderer as SvgComponent
                if (myUseCanvas) {
                    // SVG on canvas
                    val svgCanvasFigure = SvgCanvasFigure()
                    svgCanvasFigure.svgGElement.children().add(layerComponent.rootGroup)
                    svgCanvasFigure.setBounds(myLayoutInfo.getAbsoluteGeomBounds(myTilesOrigin))
                    myCanvasFigures.add(svgCanvasFigure)
                } else {
                    // regular SVG
                    layerComponent.moveTo(geomBounds.origin)
                    add(layerComponent)
                }
            }
        }
    }

    private fun addFacetLabels(geomBounds: DoubleRectangle) {
        // facet X label (on top of geom area)
        if (myLayoutInfo.facetXLabel != null) {
            val lab = TextLabel(myLayoutInfo.facetXLabel)
            val w = geomBounds.width
            val h = FACET_LABEL_HEIGHT
            val x = geomBounds.left + w / 2
            val y = geomBounds.top - h / 2

            lab.moveTo(x, y)
            lab.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
            lab.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
            add(lab)
        }

        // facet Y label (to the right from geom area)
        if (myLayoutInfo.facetYLabel != null) {
            val lab = TextLabel(myLayoutInfo.facetYLabel)
            val w = FACET_LABEL_HEIGHT
            val h = geomBounds.height
            val x = geomBounds.right + w / 2
            val y = geomBounds.top + h / 2

            lab.moveTo(x, y)
            lab.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
            lab.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
            lab.rotate(90.0)
            add(lab)
        }
    }

    private fun addAxis(geomBounds: DoubleRectangle) {
        // X axis (below geom area)
        if (myLayoutInfo.xAxisShown) {
            val axis = buildAxis(myScaleX, myLayoutInfo.xAxisInfo!!, myCoord, myTheme.axisX())
            axis.moveTo(DoubleVector(geomBounds.left, geomBounds.bottom))
            add(axis)
        }
        // Y axis (to the left from geom area, axis elements have negative x-positions)
        if (myLayoutInfo.yAxisShown) {
            val axis = buildAxis(myScaleY, myLayoutInfo.yAxisInfo!!, myCoord, myTheme.axisY())
            axis.moveTo(geomBounds.origin)
            add(axis)
        }
    }

    private fun buildAxis(scale: Scale2<Double>, info: AxisLayoutInfo, coord: CoordinateSystem, theme: AxisTheme): AxisComponent {
        val axis = AxisComponent(info.axisLength, info.orientation!!)
        AxisUtil.setBreaks(axis, scale, coord, info.orientation.isHorizontal)
        AxisUtil.applyLayoutInfo(axis, info)
        AxisUtil.applyTheme(axis, theme)
        if (isDebugDrawing) {
            if (info.tickLabelsBounds != null) {
                val rect = SvgRectElement(info.tickLabelsBounds)
                rect.strokeColor().set(Color.GREEN)
                rect.strokeWidth().set(1.0)
                rect.fillOpacity().set(0.0)
                axis.add(rect)
            }
        }
        return axis
    }

    private fun buildGeoms(
            sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
            overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>,
            coord: CoordinateSystem): List<GeomLayerRenderer> {

        val layerRenderers = ArrayList<GeomLayerRenderer>()
        for (layer in myLayers) {
            val rendererData = LayerRendererUtil.createLayerRendererData(layer, sharedNumericMappers, overallNumericDomains)

            val aestheticMappers = rendererData.aestheticMappers
            val aesthetics = rendererData.aesthetics

            val targetController = GeomTargetController(
                    layer.geomKind,
                    layer.locatorLookupSpec,
                    layer.contextualMapping
            )
            myTargetLocators.add(targetController)

            val ctx = GeomContextBuilder()
                    .aesthetics(aesthetics)
                    .aestheticMappers(aestheticMappers)
                    .geomTargetCollector(targetController)
                    .build()

            val pos = rendererData.pos
            val geom = layer.geom

            layerRenderers.add(SvgLayerRenderer(aesthetics, geom, pos, coord, ctx))
        }
        return layerRenderers
    }

    fun setShowAxis(showAxis: Boolean) {
        myShowAxis = showAxis
    }

    fun setUseCanvas(useCanvas: Boolean) {
        myUseCanvas = useCanvas
    }

    fun debugDrawing(): Property<Boolean> {
        return myDebugDrawing
    }

    companion object {
        private val FACET_LABEL_HEIGHT = 30.0
    }
}
