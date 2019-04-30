package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async

expect class SafeAsync<ItemT>() : Async<ItemT>