/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.plotBatik

import demo.livemap.demo.PathsDemoModel

object PathsDemoAwt {
    @JvmStatic
    fun main(args: Array<String>) {
        RawAwtDemo(::PathsDemoModel).start()
    }
}