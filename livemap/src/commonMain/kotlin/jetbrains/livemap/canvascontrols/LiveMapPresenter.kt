package jetbrains.livemap.canvascontrols

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.observable.property.Properties
import jetbrains.datalore.base.observable.property.PropertyBinding
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.livemap.BaseLiveMap

class LiveMapPresenter : Disposable {
    private val contentPresenter: CanvasContentPresenter
    private var registration = Registration.EMPTY
    private var isLoadingLiveMapRegistration = Registration.EMPTY
    private var removed = false

    private val initializing = ValueProperty(true)
    private val liveMapIsLoading = ValueProperty(true)
    val isLoading = Properties.map(Properties.or(initializing, liveMapIsLoading)) { value -> value ?: false }

    constructor() {
        contentPresenter = CanvasContentPresenter()
    }

    // for tests
    internal constructor(presenter: CanvasContentPresenter) {
        contentPresenter = presenter
    }

    fun render(canvasControl: CanvasControl, liveMap: Async<BaseLiveMap>) {
        contentPresenter.canvasControl = canvasControl

        showSpinner()
        liveMap.onResult(::showLiveMap, ::showError)
    }

    private fun showLiveMap(liveMap: BaseLiveMap) {
        check(isLoadingLiveMapRegistration === Registration.EMPTY) { "Unexpected" }

        initializing.set(false)
        isLoadingLiveMapRegistration = PropertyBinding.bindOneWay(liveMap.isLoading, liveMapIsLoading)

        setContent {
            LiveMapContent(liveMap).also {
                registration = it.addHandler(::showError)
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
        registration.dispose()
        isLoadingLiveMapRegistration.dispose()
        contentPresenter.clear()
    }
}