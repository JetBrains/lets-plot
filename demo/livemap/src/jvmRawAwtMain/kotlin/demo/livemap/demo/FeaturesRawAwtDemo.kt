/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo


object FeaturesRawAwtDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        RawAwtDemo(::FeaturesDemoModel).start()
    }
}