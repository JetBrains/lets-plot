/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap

import demo.livemap.demo.PerfDemoModel
import javax.swing.SwingUtilities

object PerfDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            DemoBaseJfx(::PerfDemoModel).show()
        }
    }
}
