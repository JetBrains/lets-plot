package jetbrains.datalore.base.async

class ThrowableCollectionException : RuntimeException {
    private val myThrowables = ArrayList<Throwable>()

    val throwables: List<Throwable>
        get() = myThrowables

    constructor(throwables: List<Throwable>) : super("size=" + throwables.size, throwables[0]) {
        myThrowables.addAll(throwables)
    }

    constructor(message: String, throwables: List<Throwable>) : super(message + "; size=" + throwables.size, throwables[0]) {
        myThrowables.addAll(throwables)
    }
}