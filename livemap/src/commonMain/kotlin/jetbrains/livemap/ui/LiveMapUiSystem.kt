/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.MapWidgetUtil.MAX_ZOOM
import jetbrains.livemap.MapWidgetUtil.MIN_ZOOM
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraScale
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.input.EventListenerComponent
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.rendering.primitives.Label
import jetbrains.livemap.core.rendering.primitives.MutableImage
import jetbrains.livemap.core.rendering.primitives.Text

class LiveMapUiSystem(
    private val myUiService: UiService,
    componentManager: EcsComponentManager,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit
) : LiveMapSystem(componentManager) {
    private lateinit var myLiveMapLocation: LiveMapLocation
    private lateinit var myZoomPlus: MutableImage
    private lateinit var myZoomMinus: MutableImage
    private lateinit var myGetCenter: MutableImage
    private lateinit var myViewport: Viewport
    private var myUiState: UiState = ResourcesLoading()

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myUiState.update()
    }

    override fun initImpl(context: LiveMapContext) {
        myViewport = context.mapRenderContext.viewport

        myLiveMapLocation = LiveMapLocation(myViewport, context.mapProjection)

        myUiService.resourceManager
            .add(KEY_PLUS, BUTTON_PLUS)
            .add(KEY_PLUS_DISABLED, BUTTON_PLUS_DISABLED)
            .add(KEY_MINUS, BUTTON_MINUS)
            .add(KEY_MINUS_DISABLED, BUTTON_MINUS_DISABLED)
            .add(KEY_GET_CENTER, BUTTON_GET_CENTER)

        initUi()
    }

    private fun initUi() {
        val padding = 13.0
        val side = 26
        val size = Vector(side, side)
        val plusOrigin = DoubleVector(padding, padding)
        val minusOrigin = plusOrigin.add(DoubleVector(0.0, side + padding))
        val getCenterOrigin = minusOrigin.add(DoubleVector(0.0, side + padding))

        myZoomPlus = MutableImage(plusOrigin, size.toDoubleVector())

        val buttonPlus = myUiService.addButton(myZoomPlus)
        addListenersToZoomButton(buttonPlus, MAX_ZOOM, 1.0)

        myZoomMinus = MutableImage(minusOrigin, size.toDoubleVector())

        val buttonMinus = myUiService.addButton(myZoomMinus)
        addListenersToZoomButton(buttonMinus, MIN_ZOOM, -1.0)

        myGetCenter = MutableImage(getCenterOrigin, size.toDoubleVector())
        val buttonGetCenter = myUiService.addButton(myGetCenter)
        addListenersToGetCenterButton(buttonGetCenter)

        val osm = Text().apply {
            color = Color.BLACK
            fontFamily = CONTRIBUTORS_FONT_FAMILY
            fontHeight = 11.0
            text = listOf(LINK_TO_OSM_CONTRIBUTORS)
        }

        val contributors = Label(DoubleVector(myViewport.size.x, 0.0), osm).apply {
            background = Color(200, 200, 200, 179)
            this.padding = 2.0
            position = Label.LabelPosition.LEFT
        }

        myUiService.addRenderable(contributors)
    }

    private fun addListenersToGetCenterButton(button: EcsEntity) {
        val listeners = button.getComponent<EventListenerComponent>()

        listeners.addClickListener { myMapLocationConsumer(myLiveMapLocation.viewLonLatRect) }

        listeners.addDoubleClickListener(InputMouseEvent::stopPropagation)
    }

    private fun addListenersToZoomButton(button: EcsEntity, disablingZoom: Int, animationDelta: Double) {
        val camera = getSingletonEntity(CameraComponent::class)
        val listeners = button.getComponent<EventListenerComponent>()

        listeners.addClickListener {
            if (camera.contains(CameraScale.CameraScaleEffectComponent::class) || camera.getComponent<CameraComponent>().zoom == disablingZoom.toDouble()) {
                return@addClickListener
            }

            CameraScale.setAnimation(
                camera,
                myViewport.center,
                myViewport.position,
                animationDelta
            )
        }

        listeners.addDoubleClickListener(InputMouseEvent::stopPropagation)
    }

    internal abstract class UiState {
        internal abstract fun update()
    }

    private inner class ResourcesLoading : UiState() {
        override fun update() {
            if (myUiService.resourceManager.isReady(
                    KEY_PLUS,
                    KEY_MINUS,
                    KEY_PLUS_DISABLED,
                    KEY_MINUS_DISABLED,
                    KEY_GET_CENTER
                )
            ) {
                myUiState = Processing()
            }
        }
    }

    private inner class Processing : UiState() {
        init {
            val res = myUiService.resourceManager

            myGetCenter.snapshot = res[KEY_GET_CENTER]
            updateZoomButtons(camera().zoom)
        }

        override fun update() {
            camera().ifZoomChanged { updateZoomButtons(camera().zoom) }
        }

        internal fun updateZoomButtons(zoom: Double) {
            val res = myUiService.resourceManager

            myZoomMinus.snapshot = if (zoom == MIN_ZOOM.toDouble()) res[KEY_MINUS_DISABLED] else res[KEY_MINUS]
            myZoomPlus.snapshot = if (zoom == MAX_ZOOM.toDouble()) res[KEY_PLUS_DISABLED] else res[KEY_PLUS]
        }
    }

    companion object {
        private const val KEY_PLUS = "img_plus"
        private const val KEY_PLUS_DISABLED = "img_plus_disable"
        private const val KEY_MINUS = "img_minus"
        private const val KEY_MINUS_DISABLED = "img_minus_disable"
        private const val KEY_GET_CENTER = "img_get_center"

        private const val BUTTON_PLUS =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAMAAADypuvZAAAAUVBMVEUAAADf39/f39/n5+fk5OTk5OTl5e"
                    + "Xl5eXk5OTm5ubl5eXl5eXm5uYAAAAQEBAgICCfn5+goKDl5eXo6Oj29vb39/f4+Pj5+fn9/f3+/v7///8nQ8gkAAAADXRSTlMAECAgX2B/gL+/z9/fDLiFVAAAAKJJREFUeNrt1tEOwi"
                    + "AMheGi2xQ2KBzc3Hj/BxXv5K41MTHKf/+lCSRNichcLMS5gZ6dF6iaTxUtyPejSFszZkMjciXy9oyJHNaiaoMloOjaAT0qHXX0WRQDJzVi74Ma+drvoBj8S5xEiH1TEKHQIhahyM2g9I"
                    + "//1L4hq1HkkPqO6OgL0aFHFpvO3OBo0h9UA5kFeZWTLWN+80isjU5OrpMhegCRuP2dffXKGwAAAABJRU5ErkJggg==")

        private const val BUTTON_MINUS =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAMAAADypuvZAAAAUVBMVEUAAADf39/f39/n5+fk5OTk5OTl5"
                    + "eXl5eXk5OTm5ubl5eXl5eXm5uYAAAAQEBAgICCfn5+goKDl5eXo6Oj29vb39/f4+Pj5+fn9/f3+/v7///8nQ8gkAAAADXRSTlMAECAgX2B/gL+/z9/fDLiFVAAAAI1JREFUeNrt1rEOw"
                    + "jAMRdEXaAtJ2qZ9JqHJ/38oYqObzYRQ7n5kS14MwN081YUB764zTcULgJnyrE1bFkaHkVKboUM4ITA3U4UeZLN1kHbUOuqoo19E27p8lHYVSsupVYXWM0q69dJp0N6P21FHf4OqHXkWm"
                    + "3kwYLI/VAPcTMl6UoTx2ycRGIOe3CcHvAAlagACEKjXQgAAAABJRU5ErkJggg==")

        private const val BUTTON_MINUS_DISABLED =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABmJLR0QA/wD/AP+gvaeTAAAA"
                    + "CXBIWXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH4wYTDA80Pt7fQwAAAaRJREFUaN7t2jFqAkEUBuB/xt1XiKwGwWqLbBBSWecEtltEG61yg+QCabyBrZU2Wm2jp0gn2McUCxJBcEUXdpQx"
                    + "RbIJadJo4WzeX07x4OPNNMMv8JX5fF4ioqcgCO4dx6nBgMRx/Or7fsd13UF6JgBgsVhcTyaTFyKqwMAopZb1ev3O87w3AQC9Xu+diCpSShQKBViWBSGECRDsdjtorVPUrQzD8CHFlEol"
                    + "2LZtBAYAiAjFYhFSShBRhYgec9VqNbBt+yrdjGkRQsCyLCRJgul0Wpb5fP4m1ZqaXC4HAHAcpyaRgUj5w8gE6BeOQQxiEIMYxCAGMYhBDGIQg/4p6CyfCMPhEKPR6KQZrVYL7Xb7MjZ0"
                    + "KuZcM/gN/XVdLmEGAIh+v38EgHK5bPRmVqsVXzkGMYhBDGIQgxjEIAYxiEEMyiToeDxmA7TZbGYAcDgcjEUkSQLgs24mG41GAADb7dbILWmtEccxAMD3/Y5USnWVUkutNdbrNZRSxkD2"
                    + "+z2iKPqul7muO8hmATBNGIYP4/H4OW1oXXqiKJo1m81AKdX1PG8NAB90n6KaLrmkCQAAAABJRU5ErkJggg==")

        private const val BUTTON_PLUS_DISABLED =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABmJLR0QA/wD/AP+gvaeTAAAAC"
                    + "XBIWXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH4wYTDBAFolrR5wAAAdlJREFUaN7t2j9v2kAYBvDnDvsdEDJUSEwe6gipU+Z+AkZ7KCww5Rs0XyBLvkFWJrIckxf8KbohZS8dLKFGQsIIL"
                    + "PlAR4fE/adEaiWScOh9JsuDrZ/v7hmsV+Axs9msQUSXcRx/8jzvHBYkz/OvURRd+75/W94TADCfz98nSfKFiFqwMFrr+06n8zEIgm8CAIbD4XciakkpUavV4DgOhBA2QLDZbGCMKVEfZ"
                    + "JqmFyWm0WjAdV0rMABARKjX65BSgohaRPS50m63Y9d135UrY1uEEHAcB0VRYDqdNmW1Wj0rtbamUqkAADzPO5c4gUj5i3ESoD9wDGIQgxjEIAYxyCKQUgphGCIMQyil7AeNx+Mnr3nLM"
                    + "YhBDHqVHOQnglLqnxssDMMn7/f7fQwGg+NYoUPU8aEqnc/Qc9vlGJ4BAGI0Gu0BoNlsvsgX+/vMJEnyIu9ZLBa85RjEIAa9Aej3Oj5UNb9pbb9WuLYZxCAGMYhBDGLQf4D2+/1pgFar1"
                    + "R0A7HY7axFFUQB4GDeT3W43BoD1em3lKhljkOc5ACCKomuptb7RWt8bY7BcLqG1tgay3W6RZdnP8TLf929PcwCwTJqmF5PJ5Kqc0Dr2ZFl21+v1Yq31TRAESwD4AcX3uBFfeFCxAAAAA"
                    + "ElFTkSuQmCC")

        private const val BUTTON_GET_CENTER =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABmJLR0QA/wD/AP+gvaeTAAAACXBI"
                    + "WXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH4wYcCCsV3DWWMQAAAc9JREFUaN7tmkGu2jAQhv+xE0BsEjYsgAW5Ae8Ej96EG7x3BHIDeoSepNyg3CAsQtgGNkFGeLp4hNcu2kIaXnE6vxQp"
                    + "ika2P2Xs8YyGcFaSJGGr1XolomdmnsINrZh5MRqNvpQfCAC22+2Ymb8y8xhuam2M+RRF0ZoAIMuyhJnHWmv0ej34vg8ieniKw+GA3W6H0+lUQj3pNE1nAGZaa/T7fXie5wQMAHieh263"
                    + "i6IowMyh1vqgiOgFAIIgcAbkRymlEIbh2/4hmioAEwDodDpwVb7vAwCYearQACn1jtEIoJ/gBKgpQHEcg4iueuI4/vDxLjeFzWbDADAYDH5veOORzswfOl6WZbKHrtZ8Pq/Fpooqu9yf"
                    + "XOCvF3bjfOJyAiRAAiRAv4wb94ohdcx3dRx6dEkcEiABEiAB+n9qCrfk+FVVdb5KCR4RwVrbnATv3tmq7CEBEiAB+vdA965tV16X1LabWFOow7bu8aSmIMe2ANUM9Mg36JuAiGgJAMYY"
                    + "ZyGKoihfV4qZlwCQ57mTf8lai/1+X3rZgpIkCdvt9reyvSwIAif6fqy1OB6PyPP80l42HA6jZjYAlkrTdHZuN5u4QMHMSyJaGmM+R1GUA8B3Hdvtjp1TGh0AAAAASUVORK5CYII=")

        private const val LINK_TO_OSM_CONTRIBUTORS = "Map data \u00a9 OpenStreetMap contributors"
        private const val CONTRIBUTORS_FONT_FAMILY =
            "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Helvetica, Arial, sans-serif, " + "\"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
    }
}