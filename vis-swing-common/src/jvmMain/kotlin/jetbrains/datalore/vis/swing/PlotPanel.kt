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
    refreshRate: Int = 500  // ms
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
            // Build the plot component now with its default size.
            // So that the container could take plot's preferred size in account.
            rebuildProvidedComponent(null)
        } else {
            null
        }

        addComponentListener(
            ResizeHook(
                plotPanel = this,
                lastProvidedComponent = providedComponent,
                plotPreferredSize = { containerSize: Dimension -> plotComponentProvider.getPreferredSize(containerSize) },
                plotComponentFactory = { containerSize: Dimension -> rebuildProvidedComponent(containerSize) },
                thumbnailIconConsumer = null,
                application = application,
                refreshRate = refreshRate
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
        plotPanel: PlotPanel,
        private var lastProvidedComponent: JComponent?,
        private val plotPreferredSize: (Dimension) -> Dimension,
        private val plotComponentFactory: (Dimension) -> JComponent,
        private var thumbnailIconConsumer: Consumer<in ImageIcon?>?,
        private val application: ApplicationContext,
        refreshRate: Int // ms

    ) : ComponentAdapter() {
        private var skipThisRun = lastProvidedComponent != null

        private var lastPreferredSize: Dimension? = null

        private val refreshTimer: Timer = Timer(refreshRate) {
            rebuildPlotComponent(plotPanel)
        }.apply { isRepeats = false }

        override fun componentResized(e: ComponentEvent?) {
            if (!refreshTimer.isRunning && skipThisRun) {
                // When in IDEA pligin we can not modify our state
                // withou letting the application know.
                application.runWriteAction() {
                    skipThisRun = false
                }
                return
            }
            refreshTimer.stop()
            refreshTimer.restart()
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

            application.invokeLater(action) {
                // "expired"
                // Other timer is running? Weird but lets wait for the next action.
                refreshTimer.isRunning
            }
        }
    }
}