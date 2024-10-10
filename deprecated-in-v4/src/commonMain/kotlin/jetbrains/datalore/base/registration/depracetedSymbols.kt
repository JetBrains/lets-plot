/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.registration

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.commons.registration\"",
    ReplaceWith("org.jetbrains.letsPlot.commons.registration.Registration"), level = DeprecationLevel.ERROR
)
typealias Registration = org.jetbrains.letsPlot.commons.registration.Registration

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.commons.registration\"",
    ReplaceWith("org.jetbrains.letsPlot.commons.registration.Disposable"), level = DeprecationLevel.ERROR
)
typealias Disposable = org.jetbrains.letsPlot.commons.registration.Disposable

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.commons.registration\"",
    ReplaceWith("org.jetbrains.letsPlot.commons.registration.DisposingHub"), level = DeprecationLevel.ERROR
)
typealias DisposingHub = org.jetbrains.letsPlot.commons.registration.DisposingHub

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.commons.registration\"",
    ReplaceWith("org.jetbrains.letsPlot.commons.registration.CompositeRegistration"), level = DeprecationLevel.ERROR
)
typealias CompositeRegistration = org.jetbrains.letsPlot.commons.registration.CompositeRegistration

@Deprecated(
    "Moved to package \"org.jetbrains.letsPlot.commons.registration\"",
    ReplaceWith("org.jetbrains.letsPlot.commons.registration.DisposableRegistration"), level = DeprecationLevel.ERROR
)
typealias DisposableRegistration = org.jetbrains.letsPlot.commons.registration.DisposableRegistration
