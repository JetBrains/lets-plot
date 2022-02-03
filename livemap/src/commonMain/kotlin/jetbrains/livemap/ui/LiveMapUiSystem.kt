/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.core.BusyStateComponent
import jetbrains.livemap.core.Clipboard
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.graphics.*
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.core.layers.CanvasLayerComponent
import jetbrains.livemap.core.layers.LayerGroup
import jetbrains.livemap.core.layers.LayerManager
import jetbrains.livemap.makegeometrywidget.MakeGeometryWidgetComponent
import jetbrains.livemap.makegeometrywidget.createFormattedGeometryString
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.Camera
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.toDoubleVector
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_GET_CENTER
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MAKE_GEOMETRY
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MAKE_GEOMETRY_ACTIVE
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MINUS
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MINUS_DISABLED
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_PLUS
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_PLUS_DISABLED
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_RESET_POSITION

class LiveMapUiSystem(
    private val uiService: UiService,
    private val myResourceManager: ResourceManager,
    componentManager: EcsComponentManager,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit,
    private val myLayerManager: LayerManager,
    private val myAttribution: String?,
    private val showCoordPickTools: Boolean,
    private val showResetPositionAction: Boolean,
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myCamera: Camera
    private lateinit var myMakeGeometryButton: Button
    private lateinit var mySpinner: Spinner
    private lateinit var myLiveMapLocation: LiveMapLocation
    private lateinit var myViewport: Viewport

    private var myUiState: UiState = ResourcesLoading()


    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myUiState.update(context)
    }

    override fun initImpl(context: LiveMapContext) {
        myViewport = context.mapRenderContext.viewport
        myCamera = context.camera
        myLiveMapLocation = LiveMapLocation(myViewport, context.mapProjection)
    }

    private fun finishDrawing() {
        val widget = getSingletonEntity(MakeGeometryWidgetComponent::class)

        Clipboard.copy(createFormattedGeometryString(widget.get<MakeGeometryWidgetComponent>().points))

        widget.get<LayerEntitiesComponent>().entities
            .run(::getEntitiesById)
            .forEach(EcsEntity::remove)

        myLayerManager.removeLayer(LayerGroup.FEATURES, widget.get<CanvasLayerComponent>().canvasLayer)

        widget.remove()
    }

    private fun activateCreateWidget() {
        createEntity("make_geometry_widget")
            .addComponents {
                +myLayerManager.addLayer("make_geometry_layer", LayerGroup.FEATURES)
                +LayerEntitiesComponent()
                +MouseInputComponent()
                +MakeGeometryWidgetComponent()
            }
    }

    internal abstract class UiState {
        internal abstract fun update(context: LiveMapContext)
    }

    private inner class ResourcesLoading : UiState() {
        override fun update(context: LiveMapContext) {
            if (myResourceManager.isReady()) {
                myUiState = Processing().apply { initialize(context) }
            }
        }
    }

    private inner class Processing : UiState() {
        private var shouldShowSpinner: Boolean = false
        private var shouldShowResetPosition: Boolean = false
        private val verticalPanel = mutableListOf<RenderBox>()
        private lateinit var myZoomPlusButton: Button
        private lateinit var myZoomMinusButton: Button
        private lateinit var myResetPositionButton: Button
        private lateinit var myGetCenterButton: Button

        fun initialize(context: LiveMapContext) {
            mySpinner = newSpinner()

            myZoomPlusButton = newButton(
                name = "zoom_plus",
                enabledVisualKey = KEY_PLUS,
                disabledVisualKey = KEY_PLUS_DISABLED
            ) { _, _ ->
                myCamera.animate(myCamera.zoom + 1.0, myViewport.position)
            }

            myZoomMinusButton = newButton(
                name = "zoom_minus",
                enabledVisualKey = KEY_MINUS,
                disabledVisualKey = KEY_MINUS_DISABLED
            ) { _, _ ->
                myCamera.animate(myCamera.zoom - 1.0, myViewport.position)
            }

            myResetPositionButton = newButton(
                name = "reset_position",
                enabledVisualKey = KEY_RESET_POSITION
            ) { _, _ ->
                myCamera.reset()
            }

            myGetCenterButton = newButton(
                name = "get_map_position",
                enabledVisualKey = KEY_GET_CENTER
            ) { _, _ ->
                myMapLocationConsumer(myLiveMapLocation.viewLonLatRect)
            }

            myMakeGeometryButton = newButton(
                name = "path_painter",
                enabledVisualKey = KEY_MAKE_GEOMETRY
            ) { source, _ ->
                if (containsEntity(MakeGeometryWidgetComponent::class)) {
                    finishDrawing()
                    source.enabledVisual = loadIcon(KEY_MAKE_GEOMETRY)
                } else {
                    activateCreateWidget()
                    source.enabledVisual = loadIcon(KEY_MAKE_GEOMETRY_ACTIVE)
                }
            }

            if (myAttribution != null) {
                val attribution = newAttribution().apply {
                    text = myAttribution
                    origin = myViewport.size.toDoubleVector()
                    background = Color(200, 200, 200, 179)
                    padding = 2.0
                    horizontalAlignment = Attribution.Alignment.HorizontalAlignment.LEFT
                    verticalAlignment = Attribution.Alignment.VerticalAlignment.BOTTOM
                }

                uiService.addToRenderer(attribution)
            }

            updateVerticalPanel()
            updateZoomButtons(context.camera.zoom)
        }

        private fun updateVerticalPanel() {
            verticalPanel.forEach(uiService::removeFromRenderer)
            verticalPanel.clear()

            verticalPanel.add(myZoomPlusButton)
            verticalPanel.add(myZoomMinusButton)

            if (showResetPositionAction && shouldShowResetPosition) {
                verticalPanel.add(myResetPositionButton)
            }

            if (showCoordPickTools) {
                verticalPanel.add(myGetCenterButton)
                verticalPanel.add(myMakeGeometryButton)
            }

            if (shouldShowSpinner) {
                mySpinner.runAnimation()
                verticalPanel.add(mySpinner)
            } else {
                mySpinner.stopAnimation()
            }
            verticalPanel.forEach(uiService::addToRenderer)

            // layout
            val padding = 13.0
            val step = DoubleVector(0.0, padding + iconSize.y)
            var p = DoubleVector(padding, padding)
            verticalPanel.forEach {
                it.origin = p
                p = p.add(step)
            }
            uiService.repaint()
        }

        override fun update(context: LiveMapContext) {
            shouldShowResetPosition = context.camera.canReset
            shouldShowSpinner = componentManager.containsEntity<BusyStateComponent>()
            if (shouldShowResetPosition != (myResetPositionButton in verticalPanel)) {
                updateVerticalPanel()
            } else if (shouldShowSpinner != (mySpinner in verticalPanel)) {
                updateVerticalPanel()
            }

            updateZoomButtons(context.camera.zoom)
        }

        internal fun updateZoomButtons(zoom: Double) {
            myZoomPlusButton.enabled = zoom != myViewport.maxZoom.toDouble()
            myZoomMinusButton.enabled = zoom != myViewport.minZoom.toDouble()
        }

        private fun loadIcon(key: String, dim: DoubleVector = iconSize): Image {
            return myResourceManager[key].let {
                Image().apply {
                    attach(uiService)
                    snapshot = it
                    dimension = dim
                }
            }
        }

        private fun newButton(
            name: String,
            enabledVisualKey: String,
            disabledVisualKey: String? = null,
            clickHandler: (Button, InputMouseEvent) -> Unit = { _, _ -> },
        ): Button = Button(name).apply {
            attach(uiService)
            enabledVisual = loadIcon(enabledVisualKey)
            disabledVisual = disabledVisualKey?.let { loadIcon(disabledVisualKey) }
            dimension = iconSize
            onDoubleClick = InputMouseEvent::stopPropagation
            onClick = { e ->
                e.stopPropagation()
                clickHandler(this, e)
            }
        }

        private fun newSpinner(): Spinner = Spinner().apply {
            attach(uiService)
            size = iconSide
        }

        private fun newAttribution(): Attribution = Attribution().apply {
            attach(uiService)
        }
    }

    companion object {
        private const val iconSide = 26.0
        private val iconSize = DoubleVector(iconSide, iconSide)
    }
}
