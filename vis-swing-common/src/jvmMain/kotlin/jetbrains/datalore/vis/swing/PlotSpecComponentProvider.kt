package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.plot.PlotSizeHelper
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JScrollPane
import kotlin.math.ceil
import kotlin.math.floor

abstract class PlotSpecComponentProvider(
    private val processedSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean,
    private val svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    private val executor: (() -> Unit) -> Unit,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotComponentProvider {

    override fun getPreferredSize(containerSize: Dimension): Dimension {
        return preferredFigureSize(processedSpec, preserveAspectRatio, containerSize)
    }

    override fun createComponent(containerSize: Dimension?): JComponent {
        val plotSize = containerSize?.let {
            val preferredSize = getPreferredSize(containerSize)
            DoubleVector(preferredSize.width.toDouble(), preferredSize.height.toDouble())
        }

        val plotComponent = createPlotComponent(
            processedSpec, plotSize,
            svgComponentFactory,
            executor,
            computationMessagesHandler
        )

        val isGGBunch =
            !PlotConfig.isFailure(processedSpec) && PlotConfig.figSpecKind(processedSpec) == FigKind.GG_BUNCH_SPEC
        return if (isGGBunch) {
            // GGBunch is always 'original' size => add a scroll pane.
            val scrollPane = createScrollPane(plotComponent)
            containerSize?.run {
                scrollPane.preferredSize = containerSize
                scrollPane.size = containerSize
            }
            scrollPane
        } else {
            plotComponent
        }
    }

    protected abstract fun createScrollPane(plotComponent: JComponent): JScrollPane

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

        private fun preferredFigureSize(
            figureSpec: Map<String, Any>,
            preserveAspectRatio: Boolean,
            containerSize: Dimension
        ): Dimension {
            val width = containerSize.width
            val height = containerSize.height

            if (PlotConfig.isFailure(figureSpec)) {
                // just keep given size
                return Dimension(width, height)
            }

            return when (val kind = PlotConfig.figSpecKind(figureSpec)) {
                FigKind.SUBPLOTS_SPEC -> UNSUPPORTED("NOT YET SUPPORTED: $kind")

                FigKind.GG_BUNCH_SPEC -> {
                    // Don't scale GGBunch.
                    val bunchSize = PlotSizeHelper.plotBunchSize(figureSpec)
                    Dimension(ceil(bunchSize.x).toInt(), ceil(bunchSize.y).toInt())
                }

                FigKind.PLOT_SPEC -> {
                    // Singe plot has flexible size.
                    if (!preserveAspectRatio) {
                        return containerSize
                    }

                    val config = PlotConfigClientSide.create(figureSpec) { /*ignore messages*/ }
                    val defaultSize = PlotSizeHelper.singlePlotSize(
                        figureSpec,
                        plotSize = null,
                        plotMaxWidth = null,
                        plotPreferredWidth = null,
                        config.facets,
                        config.containsLiveMap
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
            }
        }
    }
}