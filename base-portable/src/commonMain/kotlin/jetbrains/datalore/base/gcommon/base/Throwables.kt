/*
 * Copyright (c) 2019 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jetbrains.datalore.base.gcommon.base

object Throwables {
    fun getRootCause(throwable: Throwable): Throwable {
        // Keep a second pointer that slowly walks the causal chain. If the fast pointer ever catches
        // the slower pointer, then there's a loop.
        var slowPointer: Throwable = throwable
        var advanceSlowPointer = false

        var cause = throwable
        while (cause.cause != null) {
            cause = cause.cause!!

            if (cause === slowPointer) {
                throw IllegalArgumentException("Loop in causal chain detected.", cause)
            }
            if (advanceSlowPointer) {
                slowPointer = slowPointer.cause!!
            }
            advanceSlowPointer = !advanceSlowPointer // only advance every other iteration
        }
        return cause
    }
}
