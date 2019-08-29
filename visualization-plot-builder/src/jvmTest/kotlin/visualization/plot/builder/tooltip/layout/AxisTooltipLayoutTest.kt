package jetbrains.datalore.visualization.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.coord
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.point
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.size
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.SHORT_STEM_LENGTH
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment.LEFT
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class AxisTooltipLayoutTest : TooltipLayoutTestBase() {
    private var factory: MeasuredTooltipBuilderFactory? = null

    @BeforeTest
    fun setUp() {
        factory = MeasuredTooltipBuilderFactory()
    }

    @Test
    fun whenXAxisTooltipPresented_AndSideTipUnderAxis_ShouldAlignSideTipAboveAxis() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(
                        defaultHorizontalTip(coord(TooltipLayoutTestBase.VIEWPORT.center.x, TooltipLayoutTestBase.VIEWPORT.bottom)).buildTooltip()
                )
                .addTooltip(
                        xAxisTip(TooltipLayoutTestBase.VIEWPORT.center.x)
                                .size(DEFAULT_FIT_TOOLTIP_SIZE)
                                .buildTooltip()
                )
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect(X_AXIS_TOOLTIP_KEY),
                expect(HORIZONTAL_TOOLTIP_KEY).tooltipY(DEFAULT_AXIS_ORIGIN.y - tooltip(HORIZONTAL_TOOLTIP_KEY).size().y)
        )
    }

    @Test
    fun whenXAxisTooltipPresented_AndDirectedDown_ShouldAlignSideTipAboveAxisTooltip() {
        val targetCoord = coord(TooltipLayoutTestBase.VIEWPORT.center.x, DEFAULT_AXIS_ORIGIN.y - TooltipLayoutTestBase.DEFAULT_TOOLTIP_SIZE.y / 2)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(
                        defaultHorizontalTip(targetCoord).buildTooltip()
                )
                .addTooltip(
                        xAxisTip(TooltipLayoutTestBase.VIEWPORT.center.x)
                                .size(DEFAULT_NON_FIT_TOOLTIP_SIZE)
                                .buildTooltip()
                )
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect(X_AXIS_TOOLTIP_KEY).tooltipY(expectedAxisTipY(X_AXIS_TOOLTIP_KEY, TOP)),
                expect(HORIZONTAL_TOOLTIP_KEY).tooltipY(tooltip(X_AXIS_TOOLTIP_KEY).coord().y - tooltip(HORIZONTAL_TOOLTIP_KEY).size().y)
        )
    }

    @Test
    fun onlyOneXAxisTooltipShouldBeShown() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(
                        defaultHorizontalTip(coord(150.0, 150.0)).buildTooltip()
                )
                .addTooltip(
                        xAxisTip(TooltipLayoutTestBase.VIEWPORT.center.x)
                                .size(DEFAULT_FIT_TOOLTIP_SIZE)
                                .buildTooltip()
                )
                .addTooltip(
                        xAxisTip(TooltipLayoutTestBase.VIEWPORT.center.x)
                                .text("another x axis tooltip should not be added")
                                .size(DEFAULT_FIT_TOOLTIP_SIZE)
                                .buildTooltip()
                )
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect(X_AXIS_TOOLTIP_KEY),
                expect(HORIZONTAL_TOOLTIP_KEY)
        )
    }

    @Test
    fun whenXAxisTooltipPresented_AndCoveredByCursor_ShouldNotMoveTooltipUp() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .cursor(
                        coord(TooltipLayoutTestBase.VIEWPORT.center.x, DEFAULT_AXIS_ORIGIN.y)
                )
                .addTooltip(
                        xAxisTip(TooltipLayoutTestBase.VIEWPORT.center.x)
                                .size(DEFAULT_FIT_TOOLTIP_SIZE)
                                .buildTooltip()
                )
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect(X_AXIS_TOOLTIP_KEY).tooltipY(expectedAxisTipY(X_AXIS_TOOLTIP_KEY, BOTTOM))
        )
    }

    @Test
    fun yAxisTooltip_WhenFit_ShouldBeAlignedToLeft() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(
                        yAxisTip(TooltipLayoutTestBase.VIEWPORT.center.y)
                                .size(DEFAULT_FIT_TOOLTIP_SIZE)
                                .buildTooltip()
                )
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect(Y_AXIS_TOOLTIP_KEY)
                        .tooltipX(expectedAxisTipX(Y_AXIS_TOOLTIP_KEY, LEFT))
                        .tooltipY(expectedSideTipY(Y_AXIS_TOOLTIP_KEY))
        )
    }

    private fun defaultHorizontalTip(targetCoord: DoubleVector): MeasuredTooltipBuilder {
        return factory!!.horizontal(HORIZONTAL_TOOLTIP_KEY, targetCoord)
                .size(DEFAULT_TOOLTIP_SIZE)
                .objectRadius(DEFAULT_OBJECT_RADIUS)
    }

    private fun xAxisTip(x: Double): MeasuredTooltipBuilder {
        return factory!!.xAxisTip(X_AXIS_TOOLTIP_KEY, coord(x, DEFAULT_AXIS_ORIGIN.y))
    }

    private fun yAxisTip(y: Double): MeasuredTooltipBuilder {
        return factory!!.yAxisTip(Y_AXIS_TOOLTIP_KEY, coord(DEFAULT_AXIS_ORIGIN.x, y))
    }

    companion object {
        private const val X_AXIS_TOOLTIP_KEY = "xaxistooltip"
        private const val Y_AXIS_TOOLTIP_KEY = "yaxistooltip"
        private const val HORIZONTAL_TOOLTIP_KEY = "sidetipkey"

        private const val DEFAULT_X_AXIS_PADDING = 30.0
        private const val DEFAULT_Y_AXIS_PADDING = 30.0
        private const val EXTRA_PADDING = 3.0

        private val DEFAULT_AXIS_ORIGIN = point(
                DEFAULT_X_AXIS_PADDING,
                TooltipLayoutTestBase.VIEWPORT.bottom - DEFAULT_Y_AXIS_PADDING
        )

        private val DEFAULT_FIT_TOOLTIP_SIZE = size(
                DEFAULT_Y_AXIS_PADDING - SHORT_STEM_LENGTH - EXTRA_PADDING,
                DEFAULT_X_AXIS_PADDING - SHORT_STEM_LENGTH - EXTRA_PADDING
        )

        private val DEFAULT_NON_FIT_TOOLTIP_SIZE = size(
                DEFAULT_Y_AXIS_PADDING + EXTRA_PADDING,
                DEFAULT_X_AXIS_PADDING + EXTRA_PADDING
        )
    }
}
