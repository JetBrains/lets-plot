/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.android.builderHW

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skia.android.builderHW.SizeConverter.boundsPxToDp
import org.jetbrains.letsPlot.skia.android.view.SvgPanel
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

internal class FigureToSkiaAndroid(
    private val buildInfo: FigureBuildInfo,
) {
    fun eval(ctx: Context): View {
        val buildInfo = buildInfo.layoutedByOuterSize()

        // TODO: livemap
        //buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
        //    val cursorServiceConfig = CursorServiceConfig()
        //    LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
        //    cursorServiceConfig
        //}


        return when (val svgRoot = buildInfo.createSvgRoot()) {
            is CompositeFigureSvgRoot -> processCompositeFigure(svgRoot, ctx)
            is PlotSvgRoot -> processPlotFigure(svgRoot, ctx)
            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun processPlotFigure(svgRoot: PlotSvgRoot, ctx: Context): View {
        if (svgRoot.isLiveMap) {
            error("LiveMap is not supported")
        } else {
            val plotContainer = PlotContainer(svgRoot)
            return SvgPanel(context = ctx).apply {
                svg = plotContainer.svg
                eventDispatcher = object : SkikoViewEventDispatcher {
                    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                        plotContainer.mouseEventPeer.dispatch(kind, e)
                    }

                    override fun addEventHandler(
                        eventSpec: MouseEventSpec,
                        eventHandler: EventHandler<MouseEvent>
                    ): Registration {
                        TODO("Not yet implemented")
                    }
                }
            }
        }
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
        ctx: Context
    ): View {
        svgRoot.ensureContentBuilt()

        val viewGroup: ViewGroup = RelativeLayout(ctx)

        fun toLayoutParams(bounds: DoubleRectangle): RelativeLayout.LayoutParams {
            val boundsDp = boundsPxToDp(bounds, ctx)
            return RelativeLayout.LayoutParams(
                boundsDp.dimension.x,
                boundsDp.dimension.y
            ).apply {
                leftMargin = boundsDp.origin.x
                topMargin = boundsDp.origin.y
            }
        }

        val rootView = SvgPanel(context = ctx).apply {
            svg = svgRoot.svg
            eventDispatcher = null
        }
//            eventDispatcher = object : SkikoViewEventDispatcher {
//                override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
//                    plotContainer.mouseEventPeer.dispatch(kind, e)
//                }
//            })

        rootView.registerDisposable(object : Disposable {
            override fun dispose() {
                svgRoot.clearContent()
            }
        })

        viewGroup.addView(rootView, toLayoutParams(svgRoot.bounds))


        //
        // Sub-plots
        //

        for (element in svgRoot.elements) {
            val elementView = if (element is PlotSvgRoot) {
                processPlotFigure(element, ctx)
            } else {
                processCompositeFigure(element as CompositeFigureSvgRoot, ctx)
            }

            // FIXME: only one tooltip should be visible among all subplots. Try to follow this recommendation:
            viewGroup.addView(elementView, toLayoutParams(element.bounds))
        }

        return viewGroup
    }
}