package jetbrains.datalore.visualization.demoUtils.jfx

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.demoUtils.swing.SwingDemoFrame
import java.awt.Dimension
import java.util.*
import javax.swing.JComponent

class SceneMapperDemoFrame(title: String,
                           private val stylesheets: List<String>,
                           size: Dimension = FRAME_SIZE) : SwingDemoFrame(title, size) {

    override fun createSvgComponent(svgRoot: SvgSvgElement): JComponent = createSvgComponent(svgRoot, stylesheets)

    companion object {
        fun showSvg(svgRoots: List<SvgSvgElement>, stylesheets: List<String>, size: DoubleVector, title: String) {
            SceneMapperDemoFrame(title, stylesheets)
                .showSvg(svgRoots, size)
        }

        fun createSvgComponent(svgRoot: SvgSvgElement, stylesheets: List<String>) = SceneMapperJfxPanel(svgRoot, stylesheets)
            .also(::hackScaleFactorUpdate)

        // hack: wait for a scale factor update and force JavaFX to redraw the scene (1000 ms may be enough, not yet found proper event)
        private fun hackScaleFactorUpdate(mapperJfxPanel: SceneMapperJfxPanel) {
            fun redrawScene() = with(mapperJfxPanel.createSceneParent()) { val v = rotate; rotate = v + 1.0; rotate = v }
            fun after(delayMs: Long, function: () -> Unit) {
                Timer().schedule(
                    object: TimerTask() { override fun run() { runOnFxThread { function() } } },
                    delayMs
                )
            }

            after(1000, ::redrawScene)
            after(2000, ::redrawScene)
        }
    }
}