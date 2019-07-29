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
    private var myRegFx = CompositeRegistration()
    private val initStylesheets = RunOnce<Scene> { it.stylesheets.addAll(stylesheets) }


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
        setScene(scene)
    }

    abstract fun createSceneParent(): Parent

    override fun paintComponent(g: Graphics?) = super.paintComponent(g)
        // Fix for HiDPI display. Force JavaFX to repaint scene with a proper scale factor by changing stylesheets.
        // Other ways to force repaint (like changing size of the scene) can work too, but also can cause artifacts.
        .also { scene?.let(initStylesheets::run) }

    protected fun regFx(disposable: Disposable) {
        assertFxThread()
        myRegFx.add(Registration.from(disposable))
    }

    protected fun regFx(reg: Registration) {
        assertFxThread()
        myRegFx.add(reg)
    }

    private inner class RunOnce<T>(private val f: (T) -> Unit) {
        private var done: Boolean = false
        fun run(arg: T) = if (done) {
        } else {
            f(arg); done = true
        }
    }

}