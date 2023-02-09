package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.Disposable
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ContainerAdapter
import java.awt.event.ContainerEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.Timer

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
//        background = Color.WHITE
        isOpaque = false

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
                applicationContext = applicationContext,
                repaintDelay = repaintDelay
            )
        )
    }

    override fun dispose() {
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
    protected open fun handleChildRemoved(child: Component) {
        // Nothing is needed.
    }

    /**
     * Invoked each time a new plot component is created.
     * Every time the plot need to be rebuilt, old plot coponent (if any) is removed from
     * this panel. Then a new plot component is created and
     * added to this paned.
     */
    protected open fun plotComponentCreated(plotComponent: JComponent) {
//        println("plotComponentRebuilt: ${plotComponent::class.simpleName}")
    }

    private fun rebuildProvidedComponent(containerSize: Dimension?): JComponent {
        removeAll()
        val providedComponent: JComponent = plotComponentProvider.createComponent(containerSize)

        // notify
        val actualPlotComponent = if (providedComponent is JScrollPane) {
            providedComponent.viewport.view as JComponent
        } else {
            providedComponent
        }
        plotComponentCreated(actualPlotComponent)

        // add
        add(providedComponent)
        return providedComponent
    }


    private class ResizeHook(
        private val plotPanel: PlotPanel,
        private var lastProvidedComponent: JComponent?,
        private val plotPreferredSize: (Dimension) -> Dimension,
        private val plotComponentFactory: (Dimension) -> JComponent,
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
                lastProvidedComponent = plotComponentFactory(plotContainerSize)

                plotPanel.revalidate()
            }

            applicationContext.invokeLater(action) {
                // "expired"
                // Other timer is running? Weird but let's wait for the next action.
                refreshTimer.isRunning
            }
        }
    }
}