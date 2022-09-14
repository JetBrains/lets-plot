/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

class FontFamily private constructor(private val myName: String) {

    override fun toString(): String {
        return myName
    }

    companion object {
        val DEFAULT_FONT_FAMILY = forName("\"Lucida Grande\", sans-serif")
        val MONOSPACED = forName("monospace")
        val SERIF = forName("serif")

        fun forName(name: String): FontFamily {
            return FontFamily(name)
        }
    }
}