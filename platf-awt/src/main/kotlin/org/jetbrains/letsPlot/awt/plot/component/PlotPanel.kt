/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotPanelToolbar.Companion.TOOLBAR_HEIGHT
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.WithFigureModel
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ContainerAdapter
import java.awt.event.ContainerEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane

open class PlotPanel constructor(
    private val plotComponentProvider: PlotComponentProvider,
    preferredSizeFromPlot: Boolean,
    private val sizingPolicy: SizingPolicy,
    repaintDelay: Int,  // ms
    applicationContext: ApplicationContext,
    private val showToolbar: Boolean = false,
) : JPanel(), WithFigureModel, Disposable {

    @Deprecated(
        message = "Removed API: use constructor with sizingPolicy parameter",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith(
            expression = "PlotPanel(plotComponentProvider = plotComponentProvider, preferredSizeFromPlot = preferredSizeFromPlot, sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio), repaintDelay = repaintDelay, applicationContext = applicationContext, showToolbar = false)",
            imports = ["org.jetbrains.letsPlot.core.util.sizing.SizingPolicy"]
        )
    )
    constructor(
        plotComponentProvider: PlotComponentProvider,
        preferredSizeFromPlot: Boolean,
        repaintDelay: Int,
        applicationContext: ApplicationContext
    ) : this(
        plotComponentProvider,
        preferredSizeFromPlot,
        SizingPolicy.fitContainerSize(preserveAspectRatio = false),
        repaintDelay,
        applicationContext,
        false
    )

    final override val figureModel: FigureModel

    // The panel that contains the plot component when a toolbar is shown.
    private lateinit var plotComponentContainer: JPanel

    init {
        // Lay out a single child component.
        // 1. FlowLayout
        // Works well, at least in corretto-17 JRE.
        // However, in some cases undesirable "animation" effects were noticed.
        // This was happening because of continuous re-layouting after parent re-size.
        // Not sure now what cases it were, maybe just in older JRE.
//        layout = FlowLayout()
        // 2. GridBagLayout
        // Works fine but causes flickering during plot downsizing
        // See issue #888: https://github.com/JetBrains/lets-plot/issues/888
//        layout = GridBagLayout()
        // 3. GridLayout, BorderLayout
        // Almost as good as FlowLayout
        layout = BorderLayout(0, 0)
        isOpaque = false
        border = null

        // Extra cleanup on 'dispose'.
        addContainerListener(object : ContainerAdapter() {
            override fun componentRemoved(e: ContainerEvent) {
                handleChildRemovedIntern(e.child)
            }
        })

        if (showToolbar) {
            // The panel that contains the plot component when a toolbar is shown.
            // Must be initialized before the first call to 'rebuildProvidedComponent()'.
            plotComponentContainer = JPanel(BorderLayout(0, 0))
                .apply { isOpaque = false; border = null }
                .also {
                    // Extra cleanup on 'dispose'.
                    addContainerListener(object : ContainerAdapter() {
                        override fun componentRemoved(e: ContainerEvent) {
                            handleChildRemovedIntern(e.child)
                        }
                    })
                }
        }

        val providedComponent = if (preferredSizeFromPlot) {
            // Build the plot component now with its default size.
            // So that the container could take the plot's preferred size into account.
            rebuildProvidedComponent(null, sizingPolicy)
        } else {
            null
        }

        figureModel = PlotPanelFigureModel(
            plotPanel = this,
            providedComponent = providedComponent,
            plotComponentFactory = { containerSize: Dimension, specOverrideList: List<Map<String, Any>> ->
                rebuildProvidedComponent(
                    containerSize,
                    sizingPolicy,
                    specOverrideList
                )
            },
            applicationContext = applicationContext,
        )

        addComponentListener(
            ResizeHook(
                plotPanel = this,
                skipFirstResizeEvent = providedComponent != null,
                plotScrollPane = if (providedComponent is JScrollPane) providedComponent else null,
                figureModel = figureModel,
                applicationContext = applicationContext,
                repaintDelay = repaintDelay
            )
        )

        if (showToolbar) {
            add(PlotPanelToolbar().also { it.attach(figureModel) }, BorderLayout.NORTH)
            add(plotComponentContainer, BorderLayout.CENTER)
        }
    }

    override fun dispose() {
        println("PlotPanel.dispose(): ${this::class.simpleName}")
        figureModel.dispose()
        if (showToolbar) {
            plotComponentContainer.removeAll()
        }
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
     * Every time the plot needs to be rebuilt, an old plot coponent (if any) is removed from
     * this panel. Then a new plot component is created and
     * added to this paned.
     */
    protected open fun plotComponentCreated(plotComponent: JComponent) {
//        println("plotComponentRebuilt: ${plotComponent::class.simpleName}")
    }

    private fun rebuildProvidedComponent(
        containerSize: Dimension?,
        sizingPolicy: SizingPolicy,
        specOverrideList: List<Map<String, Any>> = emptyList()
    ): JComponent {
        val plotComponentContainer = if (showToolbar) plotComponentContainer else this
        plotComponentContainer.removeAll()

        // Adjust the container size if we have a toolbar
        val adjustedContainerSize = if (showToolbar && containerSize != null) {
            Dimension(containerSize.width, containerSize.height - TOOLBAR_HEIGHT)
        } else {
            containerSize
        }

        val providedComponent: JComponent = plotComponentProvider.createComponent(
            adjustedContainerSize,
            sizingPolicy,
            specOverrideList
        )

        // notify
        plotComponentCreated(actualPlotComponentFromProvidedComponent(providedComponent))

        // add
        plotComponentContainer.add(providedComponent)
        return providedComponent
    }


    companion object {
        fun actualPlotComponentFromProvidedComponent(providedComponent: JComponent): JComponent {
            return if (providedComponent is JScrollPane) {
                providedComponent.viewport.view as JComponent
            } else {
                providedComponent
            }
        }
    }
}