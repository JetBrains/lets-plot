package jetbrains.datalore.base.gcommon.collect

object ExceptionAssert {
    fun assertExceptionHappened(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Exception was expected")
        } catch (ignore: RuntimeException) {
        }
    }
}
