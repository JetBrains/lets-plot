package jetbrains.livemap.demo

import javax.swing.SwingUtilities

object PointsDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            DemoBaseJfx(::PointsDemoModel).show()
        }
    }
}
