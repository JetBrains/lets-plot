/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.component.jfx

import demo.livemap.common.component.FragmentDemoModel
import javax.swing.SwingUtilities

object FragmentDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            LiveMapComponentDemoJfx(::FragmentDemoModel).show()
        }
    }
}