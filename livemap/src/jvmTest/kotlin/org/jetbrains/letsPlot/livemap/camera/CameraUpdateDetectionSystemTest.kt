/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.camera

import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.Mocks
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraInputSystem
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class CameraUpdateDetectionSystemTest : org.jetbrains.letsPlot.livemap.LiveMapTestBase() {

    override val systemsOrder= listOf(CameraInputSystem::class)

    @Before
    override fun setUp() {
        super.setUp()
        addSystem(CameraInputSystem(componentManager))
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
        myCamera.isZoomFractionChanged && myCamera.isZoomLevelChanged

    private fun checkZoomed(isTrue: Boolean) {
        assertTrue { zoomChanged() == isTrue }
    }

    private fun checkScaled(isTrue: Boolean) {
        assertTrue { myCamera.isZoomFractionChanged == isTrue }
    }
}