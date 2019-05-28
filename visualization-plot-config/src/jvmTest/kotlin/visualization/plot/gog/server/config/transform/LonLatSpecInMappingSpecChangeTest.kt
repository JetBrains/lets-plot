package jetbrains.datalore.visualization.plot.gog.server.config.transform

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.gog.config.Option.GeomName
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.DATA
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.MAPPING
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.LAYERS
import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransform
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecFinder
import jetbrains.datalore.visualization.plot.gog.server.config.transform.LonLatSpecInMappingSpecChange.Companion.GENERATED_LONLAT_COLUMN_NAME
import jetbrains.datalore.visualization.plot.gog.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LAT_KEY
import jetbrains.datalore.visualization.plot.gog.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LONLAT_SPEC_KEY
import jetbrains.datalore.visualization.plot.gog.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LONLAT_SPEC_VALUE
import jetbrains.datalore.visualization.plot.gog.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LON_KEY
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LonLatSpecInMappingSpecChangeTest {

    private lateinit var livemapLayer: MutableMap<String, Any>
    private lateinit var opt: MutableMap<String, Any>
    private lateinit var layers: MutableList<Any>
    private lateinit var livemapMapping: MutableMap<String, Any>
    private lateinit var livemapData: MutableMap<String, Any>
    private lateinit var plotData: Map<String, Any>

    @BeforeTest
    fun setUp() {
        livemapMapping = HashMap()
        livemapData = HashMap()
        livemapLayer = HashMap()
        plotData = HashMap()

        livemapLayer[GEOM] = GeomName.LIVE_MAP
        livemapLayer[MAPPING] = livemapMapping
        livemapLayer[DATA] = livemapData

        layers = ArrayList()
        layers.add(livemapLayer)

        opt = HashMap()
        opt[LAYERS] = layers
        opt[Plot.DATA] = plotData
    }

    @Test
    fun whenGeoCoordsInLivemapData_AreGood_AndTypeDouble_ShouldReplaceMappingAndData() {
        val lon = listOf(10.0, 11.0, 12.0)
        val lat = listOf(13.0, 14.0, 15.0)

        addLonLatSpec(livemapData, lon, lat)

        process()

        assertFormattedGeoData(
                listOf(
                        "10.0, 13.0",
                        "11.0, 14.0",
                        "12.0, 15.0"
                )
        )
    }

    @Test
    fun whenGeoCoordsAreGood_AndTypeString_ShouldReplaceMappingAndData() {
        val lon = listOf("10.0", "11.0", "12.0")
        val lat = listOf("13.0", "14.0", "15.0")
        addLonLatSpec(livemapData, lon, lat)

        process()

        assertFormattedGeoData(
                listOf(
                        "10.0, 13.0",
                        "11.0, 14.0",
                        "12.0, 15.0")
        )
    }

    @Test
    fun whenGeoCoordsAreGood_AndTypeInteger_ShouldReplaceMappingAndData() {
        val lon = listOf(1, 2)
        val lat = listOf(3, 4)
        addLonLatSpec(livemapData, lon, lat)

        process()

        assertFormattedGeoData(
                listOf(
                        "1.0, 3.0",
                        "2.0, 4.0"
                )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenColumnNameIsWrong_ShouldDoNothing() {
        val lon = listOf("10.", "11.", "12.")
        val lat = listOf("13.", "14.", "15.")

        // columns 'foo' and 'bar' are not present
        addLonLatSpec(livemapData, lon, lat, "foo", "bar")

        process()
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenGeoCoordsAreBad_AndTypeString_ShouldDoNothing() {
        val lon = listOf("10.", "a", "b")
        val lat = listOf("13.", "14.", "c")
        addLonLatSpec(livemapData, lon, lat)

        process()
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenGeoCoordsAreBad_AndTypeNotSupported_ShouldDoNothing() {
        val lon = listOf(emptyList<Any>(), "a", "b")
        val lat = listOf("13.", emptyList<Any>(), "c")
        addLonLatSpec(livemapData, lon, lat)

        process()
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenDataColumnsAreEmpty_ShouldDoNothing() {
        val lon = emptyList<Any>()
        val lat = emptyList<Any>()
        addLonLatSpec(livemapData, lon, lat)

        process()
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenDataColumnsHaveDifferentSize_ShouldDoNothing() {
        val lon = listOf(1.0, 2.0, 3.0)
        val lat = listOf(4.0, 5.0)
        addLonLatSpec(livemapData, lon, lat)

        process()
    }

    @Test
    fun whenNoMappedColumns_ShouldDoNothing() {

        process()

        assertEquals(0, livemapData.size.toLong())
    }

    @Test
    fun whenNotLivemap_ShouldDoNothing() {
        layers.remove(livemapLayer)

        process()
    }

    @Test
    fun onGetMappedColumns_whenMapIdIsString_ShouldReturnNull() {
        addGeoNameMapping()

        assertFalse(containsLoanLatSpec(livemapLayer))
    }

    @Test
    fun onGetMappedColumns_whenMapIdIsStringList_ShouldReturnNull() {
        addGeoNameListMapping()

        assertFalse(containsLoanLatSpec(livemapLayer))
    }

    private fun process() {
        opt = PlotSpecTransform.builderForRawSpec()
                .change(LonLatSpecInMappingSpecChange.specSelector(), LonLatSpecInMappingSpecChange())
                .build()
                .apply(opt)
    }


    private fun assertFormattedGeoData(expected: List<String>) {
        val mapData = SpecFinder(LAYERS, DATA).findSpecs(opt)[0]
        assertEquals(expected, mapData[GENERATED_LONLAT_COLUMN_NAME])
    }

    private fun addLonLatSpec(data: MutableMap<String, Any>, lon: List<*>, lat: List<*>, lonColumnName: String = LON_COLUMN, latColumnName: String = LAT_COLUMN) {
        data[LON_COLUMN] = lon
        data[LAT_COLUMN] = lat

        val lonLatSpec = HashMap<String, Any>()
        lonLatSpec[LONLAT_SPEC_KEY] = LONLAT_SPEC_VALUE
        lonLatSpec[LON_KEY] = lonColumnName
        lonLatSpec[LAT_KEY] = latColumnName

        livemapMapping[AES_MAP_ID_NAME] = lonLatSpec
    }

    private fun addGeoNameMapping() {

        livemapMapping[AES_MAP_ID_NAME] = GEO_NAME
    }

    private fun addGeoNameListMapping() {

        livemapMapping[AES_MAP_ID_NAME] = listOf(GEO_NAME)
    }

    companion object {
        internal const val GEO_COORD_FORMAT = "%s, %s"
        private const val GEO_NAME = "Texas"
        private const val LON_COLUMN = "col_with_lon"
        private const val LAT_COLUMN = "col_with_lat"
        private val AES_MAP_ID_NAME = Aes.MAP_ID.name

        private fun containsLoanLatSpec(livemapOptions: Map<*, *>?): Boolean {
            val specs = SpecFinder(MAPPING, AES_MAP_ID_NAME).findSpecs(livemapOptions!!)
            if (specs.isEmpty()) {
                return false
            }

            val lonLatSpec = specs[0]

            return (lonLatSpec.containsKey(LONLAT_SPEC_KEY)
                    && LONLAT_SPEC_VALUE == lonLatSpec[LONLAT_SPEC_KEY]
                    && lonLatSpec.containsKey(LON_KEY)
                    && lonLatSpec.containsKey(LAT_KEY))
        }
    }
}
