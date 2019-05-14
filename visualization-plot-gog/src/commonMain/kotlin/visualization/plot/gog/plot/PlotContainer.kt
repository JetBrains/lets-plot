package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.Properties.map
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.PropertyBinding.bindOneWay
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.base.canvasFigure.SvgCanvasFigure
import jetbrains.datalore.visualization.base.svg.SvgCssResource
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plot.gog.core.event3.MouseEventSource.MouseEventSpec.*
import jetbrains.datalore.visualization.plot.gog.plot.event3.MouseEventPeer
import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.TooltipInteractions
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style
import kotlin.math.max

class PlotContainer(private val myPlot: Plot, private val myPreferredSize: ReadableProperty<DoubleVector>) {
    val svg: SvgSvgElement = SvgSvgElement()
    private val myLaidOutSize: Property<DoubleVector>
    private val myDecorationsPanel = SvgGElement()
    private val myMouseMoveRect = SvgRectElement()

    private var myContentBuilt: Boolean = false
    private var myRegistrations = CompositeRegistration()

    val tileCanvasFigures: List<CanvasFigure>
        get() = myPlot.tileCanvasFigures

    val mouseEventPeer: MouseEventPeer
        get() = myPlot.mouseEventPeer

    init {
        svg.addClass(Style.PLOT_CONTAINER)

        //this rect blocks mouse_left events while cursor moves above svg tree elements (in GWT only)
        myMouseMoveRect.addClass(Style.PLOT_GLASS_PANE)
        myMouseMoveRect.opacity().set(0.0)
        updateSize(myPreferredSize.get())

        myLaidOutSize = ValueProperty(myPreferredSize.get())
        myLaidOutSize.addHandler(object : EventHandler<PropertyChangeEvent<out DoubleVector>> {
            override fun onEvent(event: PropertyChangeEvent<out DoubleVector>) {
                updateSize(myLaidOutSize.get())
            }
        })
    }

    fun ensureContentBuilt() {
        if (!myContentBuilt) {
            buildContent()
        }
    }

    fun buildContent() {
        checkState(!myContentBuilt)
        myContentBuilt = true

        svg.setStyle(object : SvgCssResource {
            override fun css(): String {
                return Style.css
            }
        })

        reg(bindOneWay(myPreferredSize, myPlot.preferredSize()))
        reg(bindOneWay(map(myPlot.laidOutSize()) { input ->
            DoubleVector(max(myPreferredSize.get().x, input.x), max(myPreferredSize.get().y, input.y))
        }, myLaidOutSize))

        svg.children().add(myPlot.rootGroup)
        if (myPlot.isInteractionsEnabled) {
            svg.children().add(myDecorationsPanel)
            svg.children().add(myMouseMoveRect)
        }

        hookupInteractions()
    }

    fun clearContent() {
        svg.children().clear()
        myDecorationsPanel.children().clear()
        myPlot.clear()
        myRegistrations.remove()
        myRegistrations = CompositeRegistration()

        myContentBuilt = true
    }

    private fun reg(registration: Registration) {
        myRegistrations.add(registration)
    }

    private fun updateSize(size: DoubleVector) {
        svg.width().set(size.x)
        svg.height().set(size.y)

        myMouseMoveRect.width().set(size.x)
        myMouseMoveRect.height().set(size.y)
    }

    private fun hookupInteractions() {
        if (myPlot.isInteractionsEnabled) {
            // ToDo: it seems that myActualSize may change
            val viewport = DoubleRectangle(DoubleVector.ZERO, myLaidOutSize.get())
            val interactions = TooltipInteractions(myDecorationsPanel, viewport)

            val onMouseMoved = { e: MouseEvent ->
                val coord = DoubleVector(e.x.toDouble(), e.y.toDouble())
                val targetTooltipSpec = myPlot.getTargetTooltipSpec(coord)
                interactions.showTooltip(coord, targetTooltipSpec)
            }
            reg(myPlot.mouseEventPeer.addEventHandler(MOUSE_MOVED, object : EventHandler<MouseEvent> {
                override fun onEvent(event: MouseEvent) {
                    onMouseMoved(event)
                }
            }))
            reg(myPlot.mouseEventPeer.addEventHandler(MOUSE_DRAGGED, object : EventHandler<MouseEvent> {
                override fun onEvent(event: MouseEvent) {
                    interactions.hideTooltip()
                }
            }))
            reg(myPlot.mouseEventPeer.addEventHandler(MOUSE_LEFT, object : EventHandler<MouseEvent> {
                override fun onEvent(event: MouseEvent) {
                    interactions.hideTooltip()
                }
            }))
        }
    }

    fun createCanvasFigure(): CanvasFigure {
        val canvasFigure = SvgCanvasFigure()
        canvasFigure.svgGElement.children().add(svg)
        canvasFigure.setBounds(DoubleRectangle(DoubleVector.ZERO, myLaidOutSize.get()))
        return canvasFigure
    }
}
