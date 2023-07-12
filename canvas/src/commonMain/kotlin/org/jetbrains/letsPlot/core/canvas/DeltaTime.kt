/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

class DeltaTime {
    private var myLastTick: Long = 0
    private var myDt: Long = 0

    fun tick(time: Long): Long {
        if (myLastTick > 0) {
            myDt = time - myLastTick
        }

        myLastTick = time
        return myDt
    }

    fun dt(): Long {
        return myDt
    }
}
