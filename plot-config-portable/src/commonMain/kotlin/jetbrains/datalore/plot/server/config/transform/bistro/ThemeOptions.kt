/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

class ThemeOptions(
    val axisTitle: Element?,
    val axisLine: Element?
) {
    companion object {
        val ELEMENT_BLANK = Element()
    }

    class Element
}
