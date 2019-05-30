package jetbrains.datalore.visualization.plotDemo

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Parent
import javafx.scene.Scene

abstract class SwingJfxPanel() : JFXPanel() {

    init {
        Platform.runLater { createScene(createSceneParent()) }
    }

    abstract fun createSceneParent(): Parent

    private fun createScene(parent: Parent) {
        val scene: Scene = Scene(parent)
        setScene(scene)
    }
}