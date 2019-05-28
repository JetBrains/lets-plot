package jetbrains.datalore.base.observable.transform

import jetbrains.datalore.base.registration.Registration

class SimpleTransformation<SourceT, TargetT>(
        override val source: SourceT,
        override val target: TargetT,
        private val myDisposeRegistration: Registration) : Transformation<SourceT, TargetT>() {

    override fun doDispose() {
        myDisposeRegistration.remove()
    }
}
