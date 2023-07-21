/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.socket

interface SocketBuilder {
    fun build(handler: SocketHandler): Socket

    abstract class BaseSocketBuilder protected constructor(val url: String) : SocketBuilder
}
