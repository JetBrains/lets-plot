/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package sizing

internal enum class SizingMode {
    FIT,
    MIN,
    SCALED;

    companion object {
        fun byNameIgnoreCase(s: String): SizingMode? {
            return entries.firstOrNull { it.name.equals(s, true) }
        }
    }
}