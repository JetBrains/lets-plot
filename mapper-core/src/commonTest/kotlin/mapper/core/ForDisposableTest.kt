package jetbrains.datalore.mapper.core

import jetbrains.datalore.base.registration.Disposable
import kotlin.test.Test
import kotlin.test.assertTrue

class ForDisposableTest {

    private val myDisposable1 = DisposableObject()
    private val myDisposable2 = DisposableObject()

    @Test
    fun forDisposable() {
        val synchronizer = Synchronizers.forDisposable(myDisposable1)
        synchronizer.detach()
        myDisposable1.assertDisposed()
    }

    @Test
    fun forDisposables() {
        val synchronizer = Synchronizers.forDisposables(myDisposable1, myDisposable2)
        synchronizer.detach()
        myDisposable1.assertDisposed()
        myDisposable2.assertDisposed()
    }

    private class DisposableObject : Disposable {

        private var isDisposed: Boolean = false

        override fun dispose() {
            isDisposed = true
        }

        internal fun assertDisposed() {
            assertTrue(isDisposed)
        }
    }
}
