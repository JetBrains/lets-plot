package jetbrains.datalore.base.async

import jetbrains.datalore.base.async.asyncAssert.AsyncAssert.Companion.assertThat
import jetbrains.datalore.base.function.Supplier
import kotlin.test.Test
import kotlin.test.fail


class AsyncsTest {

    @Test
    fun constantAsync() {
        assertThat(Asyncs.constant(239)).succeededWith(239)
    }

    @Test
    fun failureAsync() {
        assertThat(Asyncs.failure<Int>(Throwable())).failed()
    }

    @Test
    fun map() {
        val c = Asyncs.constant(239)
        val mapped = c.map { value -> value + 1 }
        assertThat(mapped).succeededWith(240)
    }

    @Test
    fun mapFailure() {
        val a = Asyncs.constant(1)
        val mapped = a.map { value -> throw RuntimeException("test") }
        assertThat(mapped).failed()
    }

    @Test(expected = IllegalArgumentException::class)
    fun ignoreHandlerException() {
        val async = SimpleAsync<Int>()
        val res = async.map { value -> value + 1 }
        res.onSuccess { throw IllegalArgumentException() }
        res.onFailure { fail() }
        async.success(1)
    }

    @Test
    fun select() {
        val c = Asyncs.constant(239)
        val assertThat = assertThat(c.flatMap { value -> Asyncs.constant(value + 1) })
        assertThat.succeededWith(240)
    }

    @Test
    fun selectException() {
        val a = Asyncs.constant(1)
        assertThat(a.flatMap<Any> { throw RuntimeException("test") }).failureIs(RuntimeException::class.java, "test")
    }

    @Test
    fun selectFirstFailure() {
        val failure = Asyncs.failure<Int>(Throwable())
        assertThat(failure.flatMap { value -> Asyncs.constant(value + 1) }).failed()
    }

    @Test
    fun selectReturnedFailure() {
        val async = Asyncs.constant(1)
        assertThat(async.flatMap { Asyncs.failure<Int>(Throwable()) }).failed()
    }

    @Test
    fun selectReturnsNull() {
        val async = Asyncs.constant(1)
        assertThat(async.flatMap<Int> { null }).succeededWith(null)
    }

//    @Test
//    fun parallelSuccess() {
//        val parallel = Asyncs.parallel(Asyncs.constant(1), Asyncs.constant(2))
//        assertThat(parallel).succeeded()
//    }
//
//    @Test
//    fun parallelFailure() {
//        val exception = RuntimeException()
//        assertThat(Asyncs.parallel(Asyncs.constant(1), Asyncs.failure(exception))).failure().isSameAs(exception)
//    }
//
//    @Test
//    fun parallelFailureMultipleExceptions() {
//        assertThat(
//                Asyncs.parallel(Asyncs.constant(1), Asyncs.failure(Throwable()), Asyncs.failure(RuntimeException())))
//                .failureIs(ThrowableCollectionException::class.java)
//    }
//
//    @Test
//    fun parallelAlwaysSucceed() {
//        assertThat(Asyncs.parallel(Arrays.asList(Asyncs.constant(1), Asyncs.failure(Throwable())), true)).succeeded()
//    }
//
//    @Test
//    fun emptyParallel() {
//        assertThat(Asyncs.parallel()).succeeded()
//    }
//
//    @Test
//    fun parallelResult() {
//        assertThat(
//                Asyncs.parallelResult(Arrays.asList(
//                        Asyncs.constant(1), Asyncs.failure<Int>(Throwable()), Asyncs.constant(2))))
//                .succeededWith(Arrays.asList(1, 2))
//    }
//
//    @Test
//    fun parallelResultOrder() {
//        val first = SimpleAsync<Int>()
//        val second = SimpleAsync<Int>()
//        val async = Asyncs.parallelResult(Arrays.asList(first, second))
//        second.success(2)
//        first.success(1)
//        assertThat(async).succeededWith(Arrays.asList(1, 2))
//    }

    @Test
    fun untilSuccess() {
        assertThat(
                Asyncs.untilSuccess(object : Supplier<Async<Int>> {
                    override fun get(): Async<Int> {
                        return Asyncs.constant(1)
                    }
                }))
                .succeededWith(1)
    }

    @Test
    fun untilSuccessException() {
        assertThat(
                Asyncs.untilSuccess(object : Supplier<Async<Int>> {
                    private var myCounter = 0

                    override fun get(): Async<Int> {
                        myCounter++
                        return if (myCounter < 2) {
                            throw RuntimeException()
                        } else {
                            Asyncs.constant(myCounter)
                        }
                    }
                }))
                .succeededWith(2)
    }

    @Test
    fun untilSuccessWithFailures() {
        assertThat(
                Asyncs.untilSuccess(object : Supplier<Async<Int>> {
                    private var myCounter: Int = 0

                    override fun get(): Async<Int> {
                        return if (myCounter++ < 10) {
                            Asyncs.failure(RuntimeException())
                        } else Asyncs.constant(1)
                    }
                }))
                .succeededWith(1)
    }

}
