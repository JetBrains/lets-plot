package jetbrains.datalore.plot

import jetbrains.datalore.base.projectionGeometry.explicitVec
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LonLatParserTest {

    @Test
    fun withString_ShouldNotParse() {
        assertTrue { null == LonLatParser.parse("Double, Double") }
    }

    @Test
    fun withString_AndWrongStructure_ShouldNotParse() {
        assertTrue { LonLatParser.parse("Texas") == null }
    }

    @Test
    fun withoutSpaces_ShouldParse() {
        assertEquals(explicitVec(-1.0, 9.0), LonLatParser.parse("-1.0,9"))
    }

    @Test
    fun withSpaces_ShouldParse() {
        assertEquals(explicitVec(-1.0, 9.0), LonLatParser.parse("-1.0, 9"))
    }
}