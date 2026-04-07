/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package tools

import FigureModelJs
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicFromAnyQ
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap

internal fun FigureModelJs.asFigureModel(): FigureModel {
    return object : FigureModel {
        override fun addToolEventCallback(callback: (Map<String, Any>) -> Unit): Registration {
            this@asFigureModel.onToolEvent { event ->
                callback(dynamicObjectToMap(event))
            }
            return Registration.EMPTY
        }

        override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
            this@asFigureModel.activateInteractions(
                origin = origin,
                interactionSpecListJs = dynamicFromAnyQ(interactionSpecList.map { it.toMap() })
            )
        }

        override fun deactivateInteractions(origin: String) {
            this@asFigureModel.deactivateInteractions(origin)
        }

        override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        }

        override fun updateSpecOverride(specOverride: Map<String, Any>?) {
            this@asFigureModel.updateSpecOverride(dynamicFromAnyQ(specOverride))
        }

        override fun updateView() {
            this@asFigureModel.updateView()
        }

        override fun addDisposible(disposable: Disposable) {
        }

        override fun dispose() {
        }
    }
}
