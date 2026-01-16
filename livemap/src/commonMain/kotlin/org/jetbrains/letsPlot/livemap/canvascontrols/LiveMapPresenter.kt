/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.canvascontrols

import org.jetbrains.letsPlot.commons.intern.observable.property.Properties
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyBinding
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.LiveMap

class LiveMapPresenter : Disposable {
    private val contentPresenter: CanvasContentPresenter = CanvasContentPresenter()
    private var errorHandlerRegistration = Registration.EMPTY
    private var isLoadingLiveMapRegistration = Registration.EMPTY
    private var removed = false

    private val initializing = ValueProperty(true)
    private val liveMapIsLoading = ValueProperty(true)
    val isLoading = Properties.map(Properties.or(initializing, liveMapIsLoading)) { it == true }

    fun render(canvasControl: CanvasControl, liveMap: LiveMap) {
        contentPresenter.canvasControl = canvasControl
        showLiveMap(liveMap)
    }

    private fun showLiveMap(liveMap: LiveMap) {
        check(isLoadingLiveMapRegistration === Registration.EMPTY) { "Unexpected" }

        initializing.set(false)
        isLoadingLiveMapRegistration = PropertyBinding.bindOneWay(liveMap.isLoading, liveMapIsLoading)

        setContent {
            LiveMapContent(liveMap).also { liveMapContent ->
                errorHandlerRegistration = liveMapContent.addErrorHandler(::showError)
            }
        }
    }

    private fun showError(throwable: Throwable) {
        initializing.set(false)
        liveMapIsLoading.set(false)
        val message = throwable.message
        setContent { MessageContent(message ?: "Undefined exception") }
    }

    private fun setContent(canvasContentSupplier: () -> CanvasContent) {
        if (removed) {
            return
        }

        contentPresenter.show(canvasContentSupplier())
    }

    override fun dispose() {
        removed = true
        errorHandlerRegistration.dispose()
        isLoadingLiveMapRegistration.dispose()
        contentPresenter.clear()
    }

    fun paint(context2d: Context2d) {
        error("LiveMapPresenter is not intended to be used for direct painting. Use CanvasContentPresenter instead.")
    }
}