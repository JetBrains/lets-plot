package jetbrains.livemap.demo

import javax.swing.SwingUtilities

object FeaturesDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            DemoBaseJfx(::FeaturesDemoModel).show()
        }
    }
}
