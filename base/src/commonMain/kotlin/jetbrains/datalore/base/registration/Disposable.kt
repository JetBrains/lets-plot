package jetbrains.datalore.base.registration

interface Disposable {
    /**
     * Disposes this item. You shouldn't call this method more than once. It's recommended to throw
     * an exception in case it's called for the second time.
     */
    fun dispose()
}