/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package sizing

internal enum class SizingMode {
    FIT,            // assume the container dimension
    MIN,            // assume plot own dimention or the container dimension whichever is smaller
    SCALED,         // the dimension is computed so that plot preserves its aspect ratio
    FIXED;          // assume the plot own dimention (not a responsive mode)

    companion object {
        fun byNameIgnoreCase(s: String): SizingMode? {
            return entries.firstOrNull { it.name.equals(s, true) }
        }
    }
}