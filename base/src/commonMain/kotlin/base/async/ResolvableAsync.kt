package jetbrains.datalore.base.async

internal interface ResolvableAsync<ItemT> : Async<ItemT>, AsyncResolver<ItemT>