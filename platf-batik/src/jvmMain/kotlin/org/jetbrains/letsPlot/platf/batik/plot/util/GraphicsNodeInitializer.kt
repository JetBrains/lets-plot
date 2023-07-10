/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.batik.plot.util

import org.apache.batik.gvt.GraphicsNode
import java.util.ServiceLoader

/**
 * Applied to each graphics node that is created by [BatikMapperComponentHelper].
 * Used in IDEA to add specific change listeners to the nodes
 */
interface GraphicsNodeInitializer {

    fun initialize(node: GraphicsNode)

    companion object {
        fun initialize(node: GraphicsNode) {
            val serviceClass = GraphicsNodeInitializer::class.java
            val loader = ServiceLoader.load(serviceClass, serviceClass.classLoader)
            loader.forEach { it.initialize(node) }
        }
    }
}
