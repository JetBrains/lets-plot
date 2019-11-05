/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.PointDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

class PointDemoBatik : PointDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Point geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PointDemoBatik().show()
        }
    }
}
