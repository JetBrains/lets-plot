package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.BLUE
import jetbrains.datalore.base.values.Color.Companion.GREEN
import jetbrains.datalore.base.values.Color.Companion.RED
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.render.linetype.NamedLineType.*
import jetbrains.datalore.visualization.plot.base.render.point.NamedShape.STICK_SQUARE_TRIANGLE_UP
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil
import jetbrains.datalore.visualization.plot.base.scale.transform.Transforms
import jetbrains.datalore.visualization.plot.builder.scale.MapperProvider
import jetbrains.datalore.visualization.plot.builder.scale.mapper.LineTypeMapper
import jetbrains.datalore.visualization.plot.builder.scale.mapper.ShapeMapper
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleConfigTest {

    private fun checkRGBMapping(aes: Aes<*>, input: List<*>) {
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(aes as Aes<Any>, Color.TRANSPARENT)
        val expected = listOf(RED, GREEN, BLUE)
        checkMappingDiscrete(expected, input, mapperProvider)
    }

    private fun checkMappingDiscrete(expected: List<*>, input: List<*>, mapperProvider: MapperProvider<*>) {
        val map = mapOf(
                "var" to input
        )
        val data = DataFrameUtil.fromMap(map)

        val inputTransformed = MapperUtil.mapDiscreteDomainValuesToNumbers(input)

        val mapper = mapperProvider.createDiscreteMapper(data, DataFrameUtil.findVariableOrFail(data, "var"))
        for (i in input.indices) {
            assertEquals(expected[i], mapper.apply(inputTransformed[input[i]]))
        }
    }

    private fun checkIdentityMappingNumeric(aes: Aes<Double>, input: List<Double>) {
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(aes, Double.NaN)
        checkMappingDiscrete(input, input, mapperProvider)
        checkIdentityMappingContinuous(input, mapperProvider)
    }

    private fun checkIdentityMappingContinuous(input: List<Double>, mapperProvider: MapperProvider<Double>) {
        val map = mapOf(
                "var" to input
        )
        val data = DataFrameUtil.fromMap(map)

        val mapper = mapperProvider.createContinuousMapper(data, DataFrameUtil.findVariableOrFail(data, "var"),
                Double.NaN, Double.NaN, Transforms.IDENTITY)
        for (v in input) {
            assertEquals(v, mapper.apply(v))
        }
    }


    @Test
    fun colorIdentityMapper() {
        val inputs = listOf(
                listOf("red", "green", "blue"),
                listOf("#ff0000", "#00ff00", "#0000ff"),
                listOf("rgb(255,0,0)", "rgb(0,255,0)", "rgb(0,0,255)"),
                listOf("rgba(255,0,0,255)", "rgba(0,255,0,255)", "rgba(0,0,255,255)"),
                listOf(0xff0000, 0x00ff00, 0x0000ff),
                listOf(0xff0000.toDouble(), 0x00ff00.toDouble(), 0x0000ff.toDouble())
        )

        for (aes in Aes.values()) {
            if (aes.isColor) {
                for (input in inputs) {
                    checkRGBMapping(aes, input)
                }
            }
        }
    }

    @Test
    fun shapeIdentityMapper() {
        val input = listOf(14.0)
        val expected = listOf(STICK_SQUARE_TRIANGLE_UP)
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(Aes.SHAPE, ShapeMapper.NA_VALUE)
        checkMappingDiscrete(expected, input, mapperProvider)
    }

    @Test
    fun linetypeIdentityMapper() {
        val input = listOf(2.0, "longdash", 5.0, "twodash")
        val expected = listOf(DASHED, LONGDASH, LONGDASH, TWODASH)
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(Aes.LINETYPE, LineTypeMapper.NA_VALUE)
        checkMappingDiscrete(expected, input, mapperProvider)
    }

    @Test
    fun numericIdentityMapper() {
        val input = listOf(2.0, 3.0, 5.0, 7.0, Double.NaN)
        for (aes in Aes.values()) {
            if (aes.isNumeric) {
                checkIdentityMappingNumeric(aes as Aes<Double>, input)
            }
        }
    }

}