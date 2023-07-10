/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.plot.util

import javafx.embed.swing.JFXPanel
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color.TRANSPARENT
import javafx.scene.paint.Paint
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.platf.jfx.util.assertFxThread
import org.jetbrains.letsPlot.platf.jfx.util.runOnFxThread
import java.awt.Graphics
import javax.swing.SwingUtilities

abstract class AbstractJfxPanel(private val stylesheets: List<String>) : JFXPanel() {

    // BEGIN HACK
    private var scaleUpdated = false

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        if (scaleUpdated || scene == null) {
            return
        }

        scaleUpdated = true
        // Fix for HiDPI display. Force JavaFX to repaint scene with a proper scale factor by changing fill.
        // Other ways to force repaint (like changing stylesheets or size of the scene) can work too, but also can cause artifacts.
        runOnFxThread {
            // with(scene.stylesheets) { firstOrNull()?.let { remove(it); add(it); } }
            scene.fill = Paint.valueOf(scene.fill.toString())
        }
    }
    // END HACK


    private var myRegFx = CompositeRegistration()

    init {
        SwingUtilities.invokeLater {
            runOnFxThread { revalidateScene() }
        }
    }

    protected fun revalidateScene() {
        assertFxThread()

        myRegFx.dispose()
        myRegFx = CompositeRegistration()

        // Create 'transparent' scene to let CSS to compute its background color.
        // Note: Semi-transparent colors (alpha < 1) only look correct when
        // the background is WHITE (or other non-transparent color).
        // But in the case of live-map we need plot transparency to let
        // the map base layer to be visible.
        val scene = Scene(createSceneParent(), TRANSPARENT)
        scene.cursor = Cursor.CROSSHAIR
        scene.stylesheets.addAll(stylesheets)
        setScene(scene)
    }

    abstract fun createSceneParent(): Parent

    protected fun regFx(disposable: Disposable) {
        assertFxThread()
        myRegFx.add(Registration.from(disposable))
    }

    protected fun regFx(reg: Registration) {
        assertFxThread()
        myRegFx.add(reg)
    }


}