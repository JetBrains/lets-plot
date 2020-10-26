/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

class CursorService {
    var default: () -> Unit = {}
    var pointer: () -> Unit = {}
}