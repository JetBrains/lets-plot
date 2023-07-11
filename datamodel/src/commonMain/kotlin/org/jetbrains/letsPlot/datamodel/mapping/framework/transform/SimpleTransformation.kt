/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.transform

import org.jetbrains.letsPlot.commons.registration.Registration

class SimpleTransformation<SourceT, TargetT>(
        override val source: SourceT,
        override val target: TargetT,
        private val myDisposeRegistration: Registration
) : Transformation<SourceT, TargetT>() {

    override fun doDispose() {
        myDisposeRegistration.remove()
    }
}
