package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.base.svg.SvgCssResource
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plot.builder.event.MouseEventPeer
import jetbrains.datalore.visualization.plot.builder.interact.render.TooltipLayer
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import kotlin.math.max

class PlotContainer(
    private val plot: Plot,
    private val preferredSize: ReadableProperty<DoubleVector>
) {

    val svg: SvgSvgElement = SvgSvgElement()
    private val myDecorationLayer = SvgGElement()
    private val myMouseMoveRect = SvgRectElement()

    private var myContentBuilt: Boolean = false
    private var myRegistrations = CompositeRegistration()

    val tileCanvasFigures: List<CanvasFigure>
        get() = plot.tileCanvasFigures

    val mouseEventPeer: MouseEventPeer
        get() = plot.mouseEventPeer

    init {
        svg.addClass(Style.PLOT_CONTAINER)

        // this rect blocks mouse_left events while cursor moves above svg tree elements (in GWT only)
        myMouseMoveRect.addClass(Style.PLOT_GLASS_PANE)
        myMouseMoveRect.opacity().set(0.0)

        setSvgSize(preferredSize.get())

        plot.laidOutSize().addHandler(sizePropHandler { laidOutSize ->
            val newSvgSize = DoubleVector(
                max(preferredSize.get().x, laidOutSize.x),
                max(preferredSize.get().y, laidOutSize.y)
            )
            setSvgSize(newSvgSize)
        })

        preferredSize.addHandler(sizePropHandler { newPreferredSize ->
            if (newPreferredSize.x > 0 && newPreferredSize.y > 0) {
                revalidateContent()
            }
        })
    }

    fun ensureContentBuilt() {
        if (!myContentBuilt) {
            buildContent()
        }
    }

    private fun revalidateContent() {
        if (myContentBuilt) {
            clearContent()
            buildContent()
        }
    }

    private fun buildContent() {
        checkState(!myContentBuilt)
        myContentBuilt = true

        svg.setStyle(object : SvgCssResource {
            override fun css(): String {
                return Style.css
            }
        })

        plot.preferredSize().set(preferredSize.get())
        svg.children().add(plot.rootGroup)
        if (plot.isInteractionsEnabled) {
            svg.children().add(myDecorationLayer)
            svg.children().add(myMouseMoveRect)
            hookupInteractions()
        }
    }


    @Suppress("MemberVisibilityCanBePrivate")
    fun clearContent() {
        if (myContentBuilt) {
            myContentBuilt = false

            svg.children().clear()
            myDecorationLayer.children().clear()
            plot.clear()
            myRegistrations.remove()
            myRegistrations = CompositeRegistration()
        }
    }

    private fun reg(registration: Registration) {
        myRegistrations.add(registration)
    }

    private fun setSvgSize(size: DoubleVector) {
        svg.width().set(size.x)
        svg.height().set(size.y)

        myMouseMoveRect.width().set(size.x)
        myMouseMoveRect.height().set(size.y)
    }

    private fun hookupInteractions() {
        checkState(plot.isInteractionsEnabled)

        val viewport = DoubleRectangle(DoubleVector.ZERO, plot.laidOutSize().get())
        val tooltipLayer = TooltipLayer(myDecorationLayer, viewport)

        val onMouseMoved = { e: MouseEvent ->
            val coord = DoubleVector(e.x.toDouble(), e.y.toDouble())
            val tooltipSpecs = plot.createTooltipSpecs(coord)
            tooltipLayer.showTooltips(coord, tooltipSpecs)
        }
        reg(plot.mouseEventPeer.addEventHandler(MOUSE_MOVED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                onMouseMoved(event)
            }
        }))
        reg(plot.mouseEventPeer.addEventHandler(MOUSE_DRAGGED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                tooltipLayer.hideTooltip()
            }
        }))
        reg(plot.mouseEventPeer.addEventHandler(MOUSE_LEFT, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                tooltipLayer.hideTooltip()
            }
        }))
    }

// unused?
//    fun createCanvasFigure(): CanvasFigure {
//        val canvasFigure = SvgCanvasFigure()
//        canvasFigure.svgGElement.children().add(svg)
//        canvasFigure.setBounds(DoubleRectangle(DoubleVector.ZERO, myLaidOutSize.get()))
//        return canvasFigure
//    }

    companion object {
        private fun sizePropHandler(block: (newValue: DoubleVector) -> Unit): EventHandler<PropertyChangeEvent<out DoubleVector>> {
            return object : EventHandler<PropertyChangeEvent<out DoubleVector>> {
                override fun onEvent(event: PropertyChangeEvent<out DoubleVector>) {
                    val newValue = event.newValue
                    if (newValue != null) {
                        block.invoke(newValue)
                    }
                }
            }
        }
    }
}
