/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo


object FeaturesRawAwtDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        RawAwtDemo(::FeaturesDemoModel).start()
    }
}