/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.stat

import jetbrains.datalore.plotDemo.model.stat.BinDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

class BinDemoBatik : BinDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Bin stat")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BinDemoBatik().show()
        }
    }
}
