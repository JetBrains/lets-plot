package jetbrains.datalore.jetbrains.livemap.camera

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraUpdateComponent
import jetbrains.livemap.camera.CameraUpdateDetectionSystem
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class CameraUpdateDetectionSystemTest : LiveMapTestBase() {

    override val systemsOrder= listOf(CameraUpdateDetectionSystem::class)

    @Before
    override fun setUp() {
        super.setUp()
        addSystem(CameraUpdateDetectionSystem(componentManager))
    }

    @Test
    fun testIntegerZoomChangeOnZoomIn() {
        update(Mocks.camera(this).zoom(1.0))

        update(Mocks.camera(this).zoom(1.5))
        checkScaled(true)
        checkZoomed(false)

        update(Mocks.camera(this).zoom(2.0))
        checkScaled(true)
        checkZoomed(true)
    }

    @Test
    fun testIntegerZoomChangeOnZoomOut() {
        update(Mocks.camera(this).zoom(2.0))

        update(Mocks.camera(this).zoom(1.9))
        checkScaled(true)
        checkZoomed(false)

        update(Mocks.camera(this).zoom(1.0))
        checkScaled(true)
        checkZoomed(true)
    }

    private fun zoomChanged(): Boolean =
        camera.get<CameraUpdateComponent>().isZoomChanged
                && camera.get<CameraComponent>().zoom % 1 == 0.0

    private fun checkZoomed(isTrue: Boolean) {
        assertTrue { zoomChanged() == isTrue }
    }

    private fun checkScaled(isTrue: Boolean) {
        assertTrue { camera.get<CameraUpdateComponent>().isZoomChanged == isTrue }
    }
}