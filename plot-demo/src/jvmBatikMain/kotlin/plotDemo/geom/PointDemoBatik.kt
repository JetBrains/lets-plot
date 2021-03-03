/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.PointDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik

object PointDemoBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(PointDemo()) {
            SvgViewerDemoWindowBatik(
                "Points SVG",
                createSvgRoots(createModels())
            ).open()
        }
    }
}
