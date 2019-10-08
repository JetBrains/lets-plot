package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Mapping
import jetbrains.datalore.plot.builder.interact.TestUtil.coord
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.interact.GeomTarget
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind
import kotlin.test.assertEquals

open class TooltipSpecTestHelper {
    private lateinit var mappedDataAccessMock: jetbrains.datalore.plot.builder.interact.MappedDataAccessMock
    private lateinit var myTooltipSpecs: List<jetbrains.datalore.plot.builder.interact.TooltipSpec>
    internal lateinit var geomTargetBuilder: jetbrains.datalore.plot.builder.interact.TestingGeomTargetBuilder
        private set
    private var axisTooltipEnabled: Boolean = false
    private lateinit var nonTooltipAes: List<Aes<*>>
    private lateinit var axisAes: List<Aes<*>>

    internal fun init() {
        geomTargetBuilder =
            jetbrains.datalore.plot.builder.interact.TestingGeomTargetBuilder(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD)
        mappedDataAccessMock = jetbrains.datalore.plot.builder.interact.MappedDataAccessMock()

        setAxisTooltipEnabled(false)

        nonTooltipAes = ArrayList<Aes<*>>(listOf(Aes.X))
        axisAes = ArrayList<Aes<*>>(listOf(Aes.X))
    }

    internal fun <T> addMappedData(mapping: Mapping<T>): Mapping<T> {
        mappedDataAccessMock.add(mapping)
        return mapping
    }

    internal fun assertHint(expectedHintKind: Kind, expectedHintCoord: DoubleVector, expectedObjectRadius: Double) {
        assertHint(0, expectedHintKind, expectedHintCoord, expectedObjectRadius)
    }

    internal fun assertFill(expected: Color) {
        assertEquals(expected, myTooltipSpecs[0].fill)
    }

    private fun assertHint(index: Int, expectedHintKind: Kind, expectedHintCoord: DoubleVector, expectedObjectRadius: Double) {
        val tooltipSpec = myTooltipSpecs[index]
        assertEquals(expectedHintKind, tooltipSpec.layoutHint.kind)
        assertEquals(expectedHintCoord, tooltipSpec.layoutHint.coord)
        assertEquals(expectedObjectRadius, tooltipSpec.layoutHint.objectRadius, 0.001)
    }

    internal fun assertLines(index: Int, vararg expectedLines: String) {
        assertLines(index, listOf(*expectedLines))
    }

    private fun assertLines(index: Int, expectedLines: List<String>) {
        val tooltipSpec = myTooltipSpecs[index]
        assertEquals(expectedLines, tooltipSpec.lines)
    }

    internal fun assertTooltipsCount(expectedCount: Int) {
        assertEquals(expectedCount, myTooltipSpecs.size)
    }

    internal fun createTooltipSpecs(geomTarget: GeomTarget) {
        val tipAes = ArrayList<Aes<*>>()
        for (aes in mappedDataAccessMock.getMappedAes()) {
            if (nonTooltipAes.contains(aes)) {
                continue
            }
            tipAes.add(aes)
        }

        myTooltipSpecs = jetbrains.datalore.plot.builder.interact.TooltipSpecFactory(
            jetbrains.datalore.plot.builder.interact.GeomInteraction.createContextualMapping(
                tipAes,
                if (axisTooltipEnabled) axisAes else emptyList(),
                mappedDataAccessMock.mappedDataAccess
            ),
            DoubleVector.ZERO
        ).create(geomTarget)
    }

    internal fun buildTooltipSpecs() {
        createTooltipSpecs(geomTargetBuilder.withPathHitShape().build())
    }

    internal fun setAxisTooltipEnabled(axisTooltipEnabled: Boolean) {
        this.axisTooltipEnabled = axisTooltipEnabled
    }

    companion object {
        internal val TARGET_HIT_COORD = coord(100.0, 100.0)
        internal val TARGET_X_AXIS_COORD = coord(jetbrains.datalore.plot.builder.interact.TooltipSpecTestHelper.Companion.TARGET_HIT_COORD.x, 0.0)
        internal val CURSOR_COORD = DoubleVector(1.0, 2.0)
        internal const val OBJECT_RADIUS = 6.0
        internal const val DEFAULT_OBJECT_RADIUS = 0.0
        internal val AES_WIDTH = Aes.WIDTH
        internal val FILL_COLOR = Color.RED
    }
}
