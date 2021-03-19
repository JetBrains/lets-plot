package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.Disposable
import java.awt.*
import java.awt.event.*
import java.util.function.Consumer
import javax.swing.*

open class PlotPanel(
    private val plotComponentProvider: PlotComponentProvider,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms
    applicationContext: ApplicationContext,
) : JPanel(), Disposable {
    init {
        // Layout a single child component.
        // GridBagLayout seem to work better than FlowLayout
        // whan re-sizing component.
        layout = GridBagLayout()
        background = Color.WHITE

        // Extra clean-up on 'dispose'.
        addContainerListener(object : ContainerAdapter() {
            override fun componentRemoved(e: ContainerEvent) {
                handleChildRemovedIntern(e.child)
            }
        })

        val providedComponent = if (preferredSizeFromPlot) {
            // Build the plot component now with its default size.
            // So that the container could take plot's preferred size in account.
            rebuildProvidedComponent(null)
            // ToDo : updateThumbnailIcon()  here.
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
                applicationContext = applicationContext,
                repaintDelay = repaintDelay
            )
        )
    }

    final override fun dispose() {
        removeAll()
    }

    /**
     * Dispose the "provided" plot component.
     */
    private fun handleChildRemovedIntern(child: Component) {
        this.handleChildRemoved(child)
        when (child) {
            is Disposable -> child.dispose()
            is JScrollPane -> {
                handleChildRemovedIntern(child.viewport.view)
            }
        }
    }

    /**
     * Override for a custom disposal of child.
     */
    open fun handleChildRemoved(child: Component) {
        // Nothing is needed.
    }

    private fun rebuildProvidedComponent(containerSize: Dimension?): JComponent {
        removeAll()
        val providedComponent: JComponent = plotComponentProvider.createComponent(containerSize)
        add(providedComponent)
        return providedComponent
    }


    private class ResizeHook(
        private val plotPanel: PlotPanel,
        private var lastProvidedComponent: JComponent?,
        private val plotPreferredSize: (Dimension) -> Dimension,
        private val plotComponentFactory: (Dimension) -> JComponent,
        private var thumbnailIconConsumer: Consumer<in ImageIcon?>?,
        private val applicationContext: ApplicationContext,
        repaintDelay: Int // ms

    ) : ComponentAdapter() {
        private var skipThisRun = lastProvidedComponent != null

        private var lastPreferredSize: Dimension? = null

        private val refreshTimer: Timer = Timer(repaintDelay) {
            rebuildPlotComponent()
        }.apply { isRepeats = false }

        override fun componentResized(e: ComponentEvent?) {
            if (!refreshTimer.isRunning && skipThisRun) {
                // When in IDEA pligin we can modify our state
                // only in a command.
                applicationContext.runWriteAction() {
                    skipThisRun = false
                }
                return
            }

            refreshTimer.stop()

            if (lastProvidedComponent is JScrollPane) {
                lastProvidedComponent?.preferredSize = e?.component?.size
                lastProvidedComponent?.size = e?.component?.size
                plotPanel.revalidate()
                return
            }

            refreshTimer.restart()
        }

        private fun rebuildPlotComponent() {
            val action = Runnable {

                val plotContainerSize = plotPanel.size
                if (plotContainerSize == null) return@Runnable

                check(!(lastProvidedComponent is JScrollPane)) { "Unexpected JScrollPane" }

                // Either updating an existing "single" plot or
                // creating a new plot (single or GGBunch) for a first time.
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

                plotPanel.revalidate()
            }

            applicationContext.invokeLater(action) {
                // "expired"
                // Other timer is running? Weird but lets wait for the next action.
                refreshTimer.isRunning
            }
        }
    }
}