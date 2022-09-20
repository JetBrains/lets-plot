/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

class FontFamily constructor(
    val name: String,
    val monospaced: Boolean,
    val widthFactor: Double = 1.0
) {

    /**
     * ToDo: Old usages of FontFamily rely on `toString()` in places where a text style is generated.
     *       Need to find all old usages and update: -> `family.name`
     */
    override fun toString(): String {
        return name
    }

    companion object {
        val SERIF = FontFamily("serif", monospaced = false)
        val HELVETICA = FontFamily("Helvetica", monospaced = false)
    }
}