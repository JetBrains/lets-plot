/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Companion.variable
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecFactorySkippedAesTest : jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper() {

    @BeforeTest
    fun setUp() {
        init()
    }

    @Test
    fun whenSameVarMappedTwiceAsContinuousAndDiscrete_UseContinuousValue() {

        val commonLabel = "count"
        val var1 = variable().name(commonLabel).value("4").isContinuous(true)
        val var2 = variable().name(commonLabel).value("6").isContinuous(false)

        val widthMapping = var1.mapping(Aes.WIDTH)

        addMappedData(widthMapping)
        addMappedData(var2.mapping(Aes.SIZE))

        buildTooltipSpecs()

        assertTooltipsCount(1)
        assertLines(0, widthMapping.longTooltipText())
    }
}
