/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.embed.swing.JFXPanel
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.demoUtils.SceneMapperDemoFactory
import jetbrains.livemap.plotDemo.LiveMap
import java.util.concurrent.CountDownLatch
import javax.swing.SwingUtilities


object LiveMapJfx {
    @JvmStatic
    fun main(args: Array<String>) {

        val latch = CountDownLatch(1)
        SwingUtilities.invokeLater {
            JFXPanel() // initializes JavaFX environment
            latch.countDown()
        }
        latch.await()

        with(LiveMap()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            LiveMapPlotConfigDemoUtil.show(
                "LiveMap",
                plotSpecList,
                SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}