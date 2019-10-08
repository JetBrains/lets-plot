package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Companion.variable
import jetbrains.datalore.plot.builder.interact.TestUtil.rect
import jetbrains.datalore.plot.builder.interact.TestUtil.size
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class TooltipSpecFactoryHintShapeTest : jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper() {

    @BeforeTest
    fun setUp() {
        init()

        // Add mapping otherwise hint will not be created
        addMappedData(variable().mapping(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.AES_WIDTH))
    }

    @Test
    fun withPointHitShape_ShouldAddHintAroundPoint() {
        createTooltipSpecs(geomTargetBuilder.withPointHitShape(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD, 0.0).build())

        assertHint(VERTICAL_TOOLTIP,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withPathHitShape_ShouldAddHintMiddleAtY() {
        createTooltipSpecs(geomTargetBuilder.withPathHitShape().build())

        assertHint(HORIZONTAL_TOOLTIP,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withPolygonHitShape_ShouldAddHintUnderCursor() {
        createTooltipSpecs(geomTargetBuilder.withPolygonHitShape(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.CURSOR_COORD).build())

        assertHint(CURSOR_TOOLTIP,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.CURSOR_COORD,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.DEFAULT_OBJECT_RADIUS
        )
    }

    @Test
    fun withRectHitShape_ShouldAddHintMiddleAtY() {
        val dim = size(10.0, 12.0)

        createTooltipSpecs(geomTargetBuilder.withRectHitShape(rect(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD, dim)).build())

        val radius = dim.x / 2
        assertHint(HORIZONTAL_TOOLTIP,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD, radius)
    }

    @Test
    fun withLayoutHint_ShouldCopyDataFromHint() {
        addMappedData(variable().mapping(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.AES_WIDTH))

        createTooltipSpecs(geomTargetBuilder.withPathHitShape()
                .withLayoutHint(
                    jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.AES_WIDTH, TipLayoutHint.verticalTooltip(
                        jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD,
                        jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.OBJECT_RADIUS,
                        jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.FILL_COLOR
                    ))
                .build())

        assertHint(VERTICAL_TOOLTIP,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD,
            jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.OBJECT_RADIUS
        )
    }
}
