/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.input.EventListenerComponent
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.core.openLink
import jetbrains.livemap.core.rendering.Alignment
import jetbrains.livemap.core.rendering.controls.Button
import jetbrains.livemap.core.rendering.layers.CanvasLayerComponent
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.core.rendering.primitives.Attribution
import jetbrains.livemap.core.rendering.primitives.Image
import jetbrains.livemap.core.rendering.primitives.Text
import jetbrains.livemap.makegeometrywidget.MakeGeometryWidgetComponent
import jetbrains.livemap.makegeometrywidget.createFormattedGeometryString
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.Camera
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_GET_CENTER
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MAKE_GEOMETRY
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MAKE_GEOMETRY_ACTIVE
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MINUS
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_MINUS_DISABLED
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_PLUS
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_PLUS_DISABLED
import jetbrains.livemap.ui.ResourceManager.Companion.KEY_RESET_POSITION

class LiveMapUiSystem(
    private val myUiService: UiService,
    componentManager: EcsComponentManager,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit,
    private val myLayerManager: LayerManager,
    private val myAttribution: String?,
    private val showAdvancedActions: Boolean,
    private val showResetPositionAction: Boolean,
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myCamera: Camera
    private lateinit var myZoomPlusButton: Button
    private lateinit var myZoomMinusButton: Button
    private lateinit var myResetPositionButton: Button
    private lateinit var myGetCenterButton: Button
    private lateinit var myMakeGeometryButton: Button
    private lateinit var myLiveMapLocation: LiveMapLocation
    private lateinit var myViewport: Viewport
    private var myDrawingGeometry = false

    private var myUiState: UiState = ResourcesLoading()


    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myUiState.update(context)
    }

    override fun initImpl(context: LiveMapContext) {
        myViewport = context.mapRenderContext.viewport
        myCamera = context.camera
        myLiveMapLocation = LiveMapLocation(myViewport, context.mapProjection)

        initUi()
    }

    private fun initUi() {
        myZoomPlusButton = newButton(
            name = "zoom_plus",
            enabledVisual = KEY_PLUS,
            disabledVisual = KEY_PLUS_DISABLED
        ) { _, _ ->
            myCamera.animate(myCamera.zoom + 1.0, myViewport.position)
        }

        myZoomMinusButton = newButton(
            name = "zoom_minus",
            enabledVisual = KEY_MINUS,
            disabledVisual = KEY_MINUS_DISABLED
        ) { _, _ ->
            myCamera.animate(myCamera.zoom - 1.0, myViewport.position)
        }

        myResetPositionButton = newButton(
            name = "reset_position",
            enabledVisual = KEY_RESET_POSITION
        ) { _, _ ->
            myCamera.reset()
        }

        myGetCenterButton = newButton(
            name = "get_map_position",
            enabledVisual = KEY_GET_CENTER
        ) { _, _ ->
            myMapLocationConsumer(myLiveMapLocation.viewLonLatRect)
        }

        myMakeGeometryButton = newButton(
            name = "path_painter",
            enabledVisual = KEY_MAKE_GEOMETRY
        ) { source, _ ->
            if (containsEntity(MakeGeometryWidgetComponent::class)) {
                finishDrawing()
                source.enabledVisual = loadIcon(KEY_MAKE_GEOMETRY)
            } else {
                activateCreateWidget()
                source.enabledVisual = loadIcon(KEY_MAKE_GEOMETRY_ACTIVE)
            }
        }

        val buttons = mutableListOf<Button>().apply {
            add(myZoomPlusButton)
            add(myZoomMinusButton)
            if (showResetPositionAction) {
                add(myResetPositionButton)
            }
            if (showAdvancedActions) {
                add(myGetCenterButton)
                add(myMakeGeometryButton)
            }
        }

        fun layoutButtons(buttons: List<Button>) {
            val step = DoubleVector(0.0, padding + iconSize.y)
            var p = DoubleVector(padding, padding)
            buttons.forEach {
                it.position = p
                p = p.add(step)
            }
        }

        layoutButtons(buttons)
        buttons.forEach(myUiService::addButton)

        if (myAttribution != null) {
            val parts = AttributionParser(myAttribution).parse()
            val texts = ArrayList<Text>()

            for (part in parts) {
                val c = if (part is AttributionParts.SimpleLink) Color(26, 13, 171) else Color.BLACK

                val attributionText = Text().apply {
                    color = c
                    fontFamily = CONTRIBUTORS_FONT_FAMILY
                    fontSize = 11.0
                    text = listOf(part.text)
                }

                if (part is AttributionParts.SimpleLink) {
                    myUiService.addLink(attributionText).let {
                        addListenerToLink(it) {
                            openLink(part.href)
                        }
                    }
                }

                texts.add(attributionText)
            }

            Attribution(DoubleVector(myViewport.size.x, myViewport.size.y), texts).apply {
                background = Color(200, 200, 200, 179)
                this.padding = 2.0
                horizontalAlignment = Alignment.HorizontalAlignment.LEFT
                verticalAlignment = Alignment.VerticalAlignment.BOTTOM
            }.run {
                myUiService.addRenderable(this)
            }
        }
    }

    private fun addListenerToLink(link: EcsEntity, hrefConsumer: () -> Unit) {
        val listeners = link.getComponent<EventListenerComponent>()

        listeners.addClickListener {
            it.stopPropagation()
            hrefConsumer()
        }
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
            if (myUiService.resourceManager.isReady()
            ) {
                myUiState = Processing().apply { initialize(context) }
            }
        }
    }

    private inner class Processing : UiState() {

        fun initialize(context: LiveMapContext) {
            updateMakeGeometryButton(drawingGeometry = false)
            updateZoomButtons(context.camera.zoom)
        }

        override fun update(context: LiveMapContext) {
            val drawingGeometry = containsEntity(MakeGeometryWidgetComponent::class)
            if (myDrawingGeometry != drawingGeometry) {
                updateMakeGeometryButton(drawingGeometry)
            }

            if (context.camera.isZoomFractionChanged) {
                updateZoomButtons(context.camera.zoom)
            }
        }

        internal fun updateMakeGeometryButton(drawingGeometry: Boolean) {
            myDrawingGeometry = drawingGeometry
        }

        internal fun updateZoomButtons(zoom: Double) {
            myZoomPlusButton.enabled = zoom != myViewport.maxZoom.toDouble()
            myZoomMinusButton.enabled = zoom != myViewport.minZoom.toDouble()
        }
    }

    private fun loadIcon(key: String, dim: DoubleVector = iconSize): Image {
        return myUiService.resourceManager[key].let {
            Image().apply {
                snapshot = it
                size = dim
            }
        }
    }

    private fun newButton(
        name: String,
        enabledVisual: String,
        disabledVisual: String? = null,
        clickHandler: (Button, InputMouseEvent) -> Unit = { _, _ -> },
    ): Button = Button(name).apply {
        this.enabledVisual = loadIcon(enabledVisual)
        this.disabledVisual = disabledVisual?.let { loadIcon(disabledVisual) }
        buttonSize = iconSize
        onDoubleClick = InputMouseEvent::stopPropagation
        onClick = { e ->
            e.stopPropagation()
            clickHandler(this, e)
        }
    }

    companion object {
        private const val iconSide = 26.0
        private const val padding = 13.0
        private val iconSize = DoubleVector(iconSide, iconSide)
        private const val CONTRIBUTORS_FONT_FAMILY =
            "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Helvetica, Arial, sans-serif, " + "\"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
    }
}
