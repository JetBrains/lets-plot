/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.customErrorComponent

import org.jetbrains.letsPlot.batik.plot.component.DefaultSwingContextBatik

val MY_APP_CONTEXT = DefaultSwingContextBatik()
val MY_AWT_EDT_EXECUTOR = DefaultSwingContextBatik.AWT_EDT_EXECUTOR