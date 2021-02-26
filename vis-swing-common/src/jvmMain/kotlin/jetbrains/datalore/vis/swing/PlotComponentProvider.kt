package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.plot.PlotSizeHelper
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent
import kotlin.math.ceil
import kotlin.math.floor

open class PlotComponentProvider(
    private var processedSpec: MutableMap<String, Any>,
    private var svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    private var executor: (() -> Unit) -> Unit,
    private var computationMessagesHandler: ((List<String>) -> Unit)
) {

    fun getPreferredSize(containerSize: Dimension): Dimension {
        return preferredFigureSize(processedSpec, containerSize)
    }

    fun createComponent(containerSize: Dimension?): JComponent {
        val plotSize = containerSize?.let {
            val preferredSize = preferredFigureSize(processedSpec, containerSize)
            DoubleVector(preferredSize.width.toDouble(), preferredSize.height.toDouble())
        }

        return createPlotComponent(
            processedSpec, plotSize,
            svgComponentFactory,
            executor,
            computationMessagesHandler
        )
    }

    companion object {
        private fun createPlotComponent(
            figureSpecProcessed: MutableMap<String, Any>,
            preferredSize: DoubleVector?,
            svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
            executor: (() -> Unit) -> Unit,
            computationMessagesHandler: ((List<String>) -> Unit)
        ): JComponent {
            return MonolithicAwt.buildPlotFromProcessedSpecs(
                plotSize = preferredSize,
                plotSpec = figureSpecProcessed,
                plotMaxWidth = null,
                svgComponentFactory = svgComponentFactory,
                executor = executor,
                computationMessagesHandler = computationMessagesHandler
            )
        }

        private fun preferredFigureSize(figureSpec: Map<String, Any>, containerSize: Dimension): Dimension {
            val width = containerSize.width
            val height = containerSize.height

            return when {
                PlotConfig.isGGBunchSpec(figureSpec) -> {
                    // don't scale GGBunch size
                    val bunchSize = PlotSizeHelper.plotBunchSize(figureSpec)
                    Dimension(ceil(bunchSize.x).toInt(), ceil(bunchSize.y).toInt())
                }
                PlotConfig.isPlotSpec(figureSpec) -> {
                    // for single plot: scale component to fit in requested size
//                    val aspectRatio = PlotSizeHelper.figureAspectRatio(figureSpec)
                    val config = PlotConfigClientSide.create(figureSpec) { /*ignore messages*/ }
                    val defaultSize = PlotSizeHelper.singlePlotSize(
                        figureSpec, null, null,
                        config.facets, config.containsLiveMap
                    )
                    val aspectRatio = defaultSize.x / defaultSize.y

                    if (aspectRatio >= 1.0) {
                        val plotHeight = width / aspectRatio
                        val scaling = if (plotHeight > height) height / plotHeight else 1.0
                        Dimension(floor(width * scaling).toInt(), floor(plotHeight * scaling).toInt())
                    } else {
                        val plotWidth = height * aspectRatio
                        val scaling = if (plotWidth > width) width / plotWidth else 1.0
                        Dimension(floor(plotWidth * scaling).toInt(), floor(height * scaling).toInt())
                    }
                }
                else ->
                    // was failure - just keep given size
                    Dimension(width, height)
            }
        }
    }
}