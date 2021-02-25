package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.Disposable
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.event.*
import java.util.function.Consumer
import javax.swing.*

/**
 * Note:
 *  - In IDEA plugin: inherit this calss and implement `com.intellij.openapi.Disposable`
 */
abstract class PlotPanel(
    private val plotComponentProvider: PlotComponentProvider,
    preferredSizeFromPlot: Boolean,
    application: ApplicationContext,
) : JPanel(), Disposable {
    init {
        layout = GridBagLayout() // to center a single child component
        background = Color.WHITE

        // Extra clean-up on 'dispose'.
        addContainerListener(object : ContainerAdapter() {
            override fun componentRemoved(e: ContainerEvent) {
                handleChildRemovedIntern(this@PlotPanel, e.child)
            }
        })

        val providedComponent = if (preferredSizeFromPlot) {
            // Presumably, the outer frame will honor the 'preferred size' of plot component
            rebuildProvidedComponent(null)
        } else {
            null
        }

        addComponentListener(
            ResizeHook(
                skipFirstRun = preferredSizeFromPlot,
                lastProvidedComponent = providedComponent,
                plotPreferredSize = { containerSize: Dimension -> plotComponentProvider.getPreferredSize(containerSize) },
                plotComponentFactory = { containerSize: Dimension -> rebuildProvidedComponent(containerSize) },
                thumbnailIconConsumer = null,
                application = application
            )
        )
    }

    /**
     * Note:
     * - In IDEA plugin: check for an instance of `com.intellij.openapi.Disposable`
     *      "is Disposable -> Disposer.dispose(child)"
     */
    protected abstract fun handleChildRemoved(child: Component)

    private fun rebuildProvidedComponent(containerSize: Dimension?): JComponent {
        removeAll()
        val providedComponent: JComponent = plotComponentProvider.createComponent(containerSize)
        add(providedComponent)
        return providedComponent
    }

    final override fun dispose() {
        removeAll()
    }

    companion object {
        /**
         * Dispose provided plot component
         */
        private fun handleChildRemovedIntern(panel: PlotPanel, child: Component) {
            panel.handleChildRemoved(child)
            when (child) {
                is Disposable -> child.dispose()
                is JScrollPane -> {
                    handleChildRemovedIntern(panel, child.viewport.view)
                }
            }
        }
    }

    private class ResizeHook(
        private var skipFirstRun: Boolean,
        private var lastProvidedComponent: JComponent?,
        private val plotPreferredSize: (Dimension) -> Dimension,
        private val plotComponentFactory: (Dimension) -> JComponent,
        private var thumbnailIconConsumer: Consumer<in ImageIcon?>?,
        private val application: ApplicationContext

    ) : ComponentAdapter() {
        private var lastPreferredSize: Dimension? = null

        private var runningTimer: Timer = Timer(0) {}

        override fun componentResized(e: ComponentEvent?) {
            if(!runningTimer.isRunning && skipFirstRun) {
                skipFirstRun = false
                return
            }
            runningTimer.stop()
            runningTimer = Timer(500, ActionListener {
                val plotContainer = e!!.component!!
                rebuildPlotComponent(plotContainer)
            }).apply {
                isRepeats = false
            }
            runningTimer.start()
        }

        private fun rebuildPlotComponent(plotContainer: Component) {
            val action = Runnable {

                val plotContainerSize = plotContainer.size
                if (plotContainerSize == null) return@Runnable

                if (lastProvidedComponent is JScrollPane) {
                    // GGBunch - do not rebuid: it is constant size.
                    lastProvidedComponent!!.preferredSize = plotContainerSize
                    lastProvidedComponent!!.size = plotContainerSize
                } else {

                    // Single plot
                    val preferredSize: Dimension = plotPreferredSize(plotContainerSize)
                    if (lastPreferredSize == preferredSize) {
                        // No change in size => no need to rebuild plot component
                        return@Runnable
                    }
                    lastPreferredSize = preferredSize
                    val updateThumbnail = lastProvidedComponent == null
                    lastProvidedComponent = plotComponentFactory(plotContainerSize)
                    if (updateThumbnail && thumbnailIconConsumer != null) {
                        // ToDo
//                        com.jetbrains.plugins.letsPlot.figure.ComponentFigure.updateThumbnailIcon(
//                            com.jetbrains.plugins.letsPlot.figure.ComponentFigure.actualPlotComponent(
//                                myLastProvidedComponent
//                            ), myThumbnailIconConsumer
//                        )
                    }

                    plotContainer.revalidate()

                    // ToDo: In IDEA plugin ("SciVew"):
                    // revalidate somethin to force tabbed pane to repaint after resizing
                    // "thumbnailIconConsumer"?
                }
            }

            application.invokeLater(action) { false }
        }
    }
}