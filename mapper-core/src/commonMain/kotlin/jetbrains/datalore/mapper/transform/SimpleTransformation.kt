/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.transform

import jetbrains.datalore.base.registration.Registration

class SimpleTransformation<SourceT, TargetT>(
        override val source: SourceT,
        override val target: TargetT,
        private val myDisposeRegistration: Registration) : Transformation<SourceT, TargetT>() {

    override fun doDispose() {
        myDisposeRegistration.remove()
    }
}
