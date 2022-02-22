/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.MultilineLabelDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(MultilineLabelDemo()) {
        SvgViewerDemoWindowJfx(
            "Multiline label anchor",
            createSvgRoots(listOf(createModel())),
            stylesheets = listOf("/text-label-demo.css")
        ).open()
    }
}