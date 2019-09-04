package jetbrains.livemap.demo

import javax.swing.SwingUtilities

object EmptyLiveMapDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            DemoBaseJfx(::EmptyLivemMapDemoModel).show()
        }
    }
}
