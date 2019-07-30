package jetbrains.datalore.visualization.demoUtils.jfx

import javafx.embed.swing.JFXPanel
import javafx.scene.Parent
import javafx.scene.Scene
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
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
        // Fix for HiDPI display. Force JavaFX to repaint scene with a proper scale factor by changing stylesheets.
        // Other ways to force repaint (like changing size of the scene) can work too, but also can cause artifacts.
        runOnFxThread { with (scene.stylesheets) { firstOrNull()?.let { remove(it); add(it); } } }
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

        val scene = Scene(createSceneParent())
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