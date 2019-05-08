package mapper.core

import io.mockk.mockk
import io.mockk.verify
import jetbrains.datalore.mapper.core.MapperTest
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.mapper.core.MappingContextListener
import kotlin.test.Test


// Mockk for JS is not yet available: https://github.com/mockk/mockk/issues/100
class MapperTestJvm {

    @Test
    fun mappingContextListeners() {
        val l = mockk<MappingContextListener>(relaxed = true)

        val ctx = MappingContext()
        ctx.addListener(l)

        val mapper = MapperTest.TestMapper(Any())
        mapper.attachRoot(ctx)
        mapper.detachRoot()

        verify { l.onMapperRegistered(mapper) }
        verify { l.onMapperUnregistered(mapper) }
    }
}