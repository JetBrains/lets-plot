/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvascontrols

import org.jetbrains.letsPlot.base.intern.async.Async
import jetbrains.datalore.base.observable.property.Properties
import jetbrains.datalore.base.observable.property.PropertyBinding
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.scheduleAsync
import jetbrains.livemap.LiveMap

class LiveMapPresenter : Disposable {
    private val contentPresenter: CanvasContentPresenter
    private var errorHandlerRegistration = Registration.EMPTY
    private var isLoadingLiveMapRegistration = Registration.EMPTY
    private var removed = false

    private val initializing = ValueProperty(true)
    private val liveMapIsLoading = ValueProperty(true)
    val isLoading = Properties.map(Properties.or(initializing, liveMapIsLoading)) { it == true }

    constructor() {
        contentPresenter = CanvasContentPresenter()
    }

    // for tests
    internal constructor(presenter: CanvasContentPresenter) {
        contentPresenter = presenter
    }

    fun render(canvasControl: CanvasControl, liveMap: Async<LiveMap>) {
        contentPresenter.canvasControl = canvasControl

        showSpinner()
        canvasControl.scheduleAsync(liveMap).onResult(::showLiveMap, ::showError)
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

    private fun showSpinner() {
        initializing.set(true)
        setContent(::SpinnerContent)
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
}