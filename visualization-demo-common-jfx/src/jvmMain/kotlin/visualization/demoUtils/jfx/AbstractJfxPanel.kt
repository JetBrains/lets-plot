package jetbrains.datalore.visualization.demoUtils.jfx

import javafx.embed.swing.JFXPanel
import javafx.scene.Parent
import javafx.scene.Scene
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import javax.swing.SwingUtilities

abstract class AbstractJfxPanel(private val stylesheets: List<String>) : JFXPanel() {
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