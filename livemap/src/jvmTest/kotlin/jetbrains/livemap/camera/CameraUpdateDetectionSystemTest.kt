/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.camera

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.livemap.camera.CameraUpdateDetectionSystem
import jetbrains.livemap.camera.isIntegerZoom
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
        myCamera.isZoomChanged && myCamera.isIntegerZoom

    private fun checkZoomed(isTrue: Boolean) {
        assertTrue { zoomChanged() == isTrue }
    }

    private fun checkScaled(isTrue: Boolean) {
        assertTrue { myCamera.isZoomChanged == isTrue }
    }
}