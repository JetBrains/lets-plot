/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

class TooltipsOptions(
    val anchor: String? = null,
    val minWidth: String? = null,
    val color: String? = null,
    val formats: Map<String, String>? = null,
    val lines: List<String>? = null
) {

    companion object {
        fun variable(name: String) = "@$name"
    }
}
