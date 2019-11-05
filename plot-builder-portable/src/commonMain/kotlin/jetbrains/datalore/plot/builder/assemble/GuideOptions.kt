/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

abstract class GuideOptions {

    var isReverse: Boolean = false

    companion object {
        val NONE: GuideOptions = object : GuideOptions() {}
    }
}
