@file:Suppress("unused")

import demo.plot.PerformanceMagickCanvasDemo
import demo.svg.SimpleMagickCanvasDemo

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


fun simpleMagickCanvasDemoMain() {
    SimpleMagickCanvasDemo.main()
}


fun performanceMain(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: pass <number of points>")
        return
    }

    val n = args[0].toIntOrNull()
        ?: run {
            println("Invalid number of points: ${args[0]}")
            return
        }

    PerformanceMagickCanvasDemo.main(n)
}
