/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.variables
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.junit.Ignore
import org.junit.Test

fun variable(df: DataFrame, varName: String) = variables(df)[varName] ?: error("Variable $varName not found")
fun values(df: DataFrame, name: String) = df.get(variable(df, name))

class DataJoinTest {
    @Test
    fun singleKey_MatchingRows() {
        // User searches names from data - same size, same order
        // Data: [USA, RU, FR]
        // Map: [USA, RU, FR]
        // Result: [USA, RU, FR]
        val data = DataFrame.Builder()
            .put(Variable("Countries"), listOf("USA", "RU", "FR"))
            .put(Variable("Values"), listOf(0.0, 1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("USA", "RU", "FR"))
            .put(Variable("found name"), listOf("United States of America", "Russia", "France"))
            .put(Variable("geometry"), listOf("usa_geometry", "ru_geometry", "fr_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Countries"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerieFrom(data, "Countries")
            .hasSerieFrom(data, "Values")
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
    }

    @Test
    fun tripleKeys_MatchingRows() {
        // User searches names from data - same size, same order
        // Data: [Anderson, Clay, Alameda]
        // Map: [Anderson, Clay, Alameda]
        // Result: [Anderson, Clay, Alameda]
        val data = DataFrame.Builder()
            .put(Variable("Countries"), listOf("USA", "USA", "USA"))
            .put(Variable("States"), listOf("TX", "AL", "CA"))
            .put(Variable("Counties"), listOf("Anderson", "Clay", "Alameda"))
            .put(Variable("Values"), listOf(0.0, 1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("Anderson", "Clay", "Alameda"))
            .put(Variable("state"), listOf("TX", "AL", "CA"))
            .put(Variable("country"), listOf("USA", "USA", "USA"))
            .put(Variable("found name"), listOf("Anderson County", "Clay County", "Alameda County"))
            .put(Variable("geometry"), listOf("anderson_geometry", "clay_geometry", "alameda_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Counties", "States", "Countries"), map, listOf("request", "state", "country"))

        assertThat(jointDataFrame)
            .hasSerieFrom(data, "Countries")
            .hasSerieFrom(data, "States")
            .hasSerieFrom(data, "Counties")
            .hasSerieFrom(data, "Values")
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "state")
            .hasSerieFrom(map, "country")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
    }

    @Test
    fun singleKey_extraMapRows() {
        // Data: [USA, RU, FR]
        // Map: [UA, USA, GER, FR, RU]
        // Result: [USA, RU, FR, UA, GER]
        val data = DataFrame.Builder()
            .put(Variable("Countries"), listOf("USA", "RU", "FR"))
            .put(Variable("Values"), listOf(0.0, 1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("UA", "USA", "GER", "FR", "RU"))
            .put(Variable("found name"), listOf("Ukraine", "United States of America", "Germany", "France", "Russia"))
            .put(Variable("geometry"), listOf("ua_geometry", "usa_geometry", "ger_geometry", "fr_geometry", "ru_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Countries"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Countries"), listOf("USA", "RU", "FR", null, null)) // nulls for UA and GER
            .hasSerie(variable(data, "Values"), listOf(0.0, 1.0, 2.0, null, null)) // nulls for UA and GER
            .hasSerie(variable(map, "request"), listOf("USA", "RU", "FR", "UA", "GER"))
            .hasSerie(variable(map, "found name"), listOf("United States of America", "Russia", "France", "Ukraine",  "Germany"))
            .hasSerie(variable(map, "geometry"), listOf("usa_geometry", "ru_geometry", "fr_geometry", "ua_geometry", "ger_geometry"))
    }


    @Test
    fun singleKey_MatchingDupsInMap() {
        // Data: [Asia, Europe]
        // Map: [Europe, Asia, Europe]
        // Result: [Asia, Europe, Europe]

        val data = DataFrame.Builder()
            .put(Variable("Continents"), listOf("Asia", "Europe"))
            .put(Variable("Values"), listOf(1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("Country"), listOf("Germany", "Japan", "France"))
            .put(Variable("Cont"), listOf("Europe", "Asia", "Europe"))
            .put(Variable("geometry"), listOf("ger_geometry", "jap_geometry", "fr_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Continents"), map, listOf("Cont"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Continents"), listOf("Asia", "Europe", "Europe"))
            .hasSerie(variable(data, "Values"), listOf(1.0, 2.0, 2.0))
            .hasSerie(variable(map, "Country"), listOf("Japan", "Germany", "France"))
            .hasSerie(variable(map, "Cont"), listOf("Asia", "Europe", "Europe"))
            .hasSerie(variable(map, "geometry"), listOf("jap_geometry", "ger_geometry", "fr_geometry"))
    }


    @Test
    fun singleKey_MissingDupsInMap() {
        // Data: [Asia]
        // Map: [Europe, Asia, Europe]
        // Result: [Asia, Europe, Europe]

        val data = DataFrame.Builder()
            .put(Variable("Continents"), listOf("Asia"))
            .put(Variable("Values"), listOf(1.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("Country"), listOf("Germany", "Japan", "France"))
            .put(Variable("Cont"), listOf("Europe", "Asia", "Europe"))
            .put(Variable("geometry"), listOf("ger_geometry", "jap_geometry", "fr_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Continents"), map, listOf("Cont"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Continents"), listOf("Asia", null, null))
            .hasSerie(variable(data, "Values"), listOf(1.0, null, null))
            .hasSerie(variable(map, "Country"), listOf("Japan", "Germany", "France"))
            .hasSerie(variable(map, "Cont"), listOf("Asia", "Europe", "Europe"))
            .hasSerie(variable(map, "geometry"), listOf("jap_geometry", "ger_geometry", "fr_geometry"))
    }

    @Test
    fun dupsInDataAndMap_takeOnlyFirstEntryFromMap() {
        // Drops France - expected behaviour. We can't predict is there multiindex in map by a single key.
        // Data: [Asia, Asia]
        // Map: [Europe, Asia, Europe]
        // Result: [Asia, Asia, Europe]

        val data = DataFrame.Builder()
            .put(Variable("Continents"), listOf("Asia", "Asia"))
            .put(Variable("Values"), listOf(1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("Country"), listOf("Germany", "Japan", "France", "Japan"))
            .put(Variable("Cont"), listOf("Europe", "Asia", "Europe", "Asia"))
            .put(Variable("geometry"), listOf("ger_geometry", "jap_geometry", "fr_geometry", "jap_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Continents"), map, listOf("Cont"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Continents"), listOf("Asia", "Asia", null))
            .hasSerie(variable(data, "Values"), listOf(1.0, 2.0, null))
            .hasSerie(variable(map, "Country"), listOf("Japan", "Japan", "Germany"))
            .hasSerie(variable(map, "Cont"), listOf("Asia", "Asia", "Europe"))
            .hasSerie(variable(map, "geometry"), listOf("jap_geometry", "jap_geometry", "ger_geometry"))
    }

    @Test
    fun tripleKey_extraMapRows() {
        // User searches names from data - same size, same order
        // Data: [Anderson, Clay, Alameda]
        // Map: [Carson, Anderson, Clay, Adams, Alameda]
        // Result: [Anderson, Clay, Alameda, Carson, Adams]
        val data = DataFrame.Builder()
            .put(Variable("Countries"), listOf("USA", "USA", "USA"))
            .put(Variable("States"), listOf("TX", "AL", "CA"))
            .put(Variable("Counties"), listOf("Anderson", "Clay", "Alameda"))
            .put(Variable("Values"), listOf(0.0, 1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("Carson", "Anderson", "Clay", "Adams", "Alameda"))
            .put(Variable("state"), listOf("NV", "TX", "AL", "CO", "CA"))
            .put(Variable("country"), listOf("USA", "USA", "USA", "USA", "USA"))
            .put(Variable("found name"), listOf("Carson County", "Anderson County", "Clay County", "Adams County", "Alameda County"))
            .put(Variable("geometry"), listOf("carson_geometry", "anderson_geometry", "clay_geometry", "adams_geometry", "alameda_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Counties", "States", "Countries"), map, listOf("request", "state", "country"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Countries"), listOf("USA", "USA", "USA", null, null))
            .hasSerie(variable(data, "States"), listOf("TX", "AL", "CA", null, null))
            .hasSerie(variable(data, "Counties"), listOf("Anderson", "Clay", "Alameda", null, null))
            .hasSerie(variable(data, "Values"), listOf(0.0, 1.0, 2.0, null, null))
            .hasSerie(variable(map, "request"), listOf("Anderson", "Clay", "Alameda", "Carson", "Adams"))
            .hasSerie(variable(map, "state"), listOf("TX", "AL", "CA", "NV", "CO"))
            .hasSerie(variable(map, "country"), listOf("USA", "USA", "USA", "USA", "USA"))
            .hasSerie(variable(map, "found name"), listOf("Anderson County", "Clay County", "Alameda County", "Carson County", "Adams County"))
            .hasSerie(variable(map, "geometry"), listOf("anderson_geometry", "clay_geometry", "alameda_geometry", "carson_geometry", "adams_geometry"))
    }


    @Test
    fun singleKey_extraDataRows() {
        // Remove data rows that not matched to a map
        // Data: [USA, RU, FR]
        // Map: [FR, RU]
        val data = DataFrame.Builder()
            .put(Variable("Countries"), listOf("USA", "RU", "FR"))
            .put(Variable("Values"), listOf(0.0, 1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("FR", "RU"))
            .put(Variable("found name"), listOf("France", "Russia"))
            .put(Variable("geometry"), listOf("fr_geometry", "ru_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Countries"), map, listOf("request"))

        // Should take variables from corresponding dataframes, not recreate them
        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Countries"), listOf("RU", "FR"))
            .hasSerie(variable(data, "Values"), listOf(1.0, 2.0))
            .hasSerie(variable(map, "request"), listOf("RU", "FR"))
            .hasSerie(variable(map, "found name"), listOf("Russia", "France"))
            .hasSerie(variable(map, "geometry"), listOf("ru_geometry", "fr_geometry"))
    }


    @Test
    fun tripleKey_extraDataRows() {
        // User searches names from data - same size, same order
        val data = DataFrame.Builder()
            .put(Variable("Countries"), listOf("USA", "USA", "USA"))
            .put(Variable("States"), listOf("TX", "AL", "CA"))
            .put(Variable("Counties"), listOf("Anderson", "Clay", "Alameda"))
            .put(Variable("Values"), listOf(0.0, 1.0, 2.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("Anderson", "Alameda"))
            .put(Variable("state"), listOf("TX", "CA"))
            .put(Variable("country"), listOf("USA", "USA"))
            .put(Variable("found name"), listOf("Anderson County", "Alameda County"))
            .put(Variable("geometry"), listOf("anderson_geometry", "alameda_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Counties", "States", "Countries"), map, listOf("request", "state", "country"))

        // Should take variables from corresponding dataframes, not recreate them
        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Countries"), listOf("USA", "USA"))
            .hasSerie(variable(data, "States"), listOf("TX", "CA"))
            .hasSerie(variable(data, "Counties"), listOf("Anderson", "Alameda"))
            .hasSerie(variable(data, "Values"), listOf(0.0, 2.0))
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "state")
            .hasSerieFrom(map, "country")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
    }

    @Test
    fun multiindex_singleKey() {
        // Data: [USA, RU, FR]
        // Map: [USA, FR, RU]
        // Result: [USA, RU, FR]
        val data = DataFrame.Builder()
            .put(Variable("Country"), listOf("USA", "USA", "RU", "RU", "FR", "FR"))
            .put(Variable("Category"), listOf("A", "B", "A", "B", "A", "B"))
            .put(Variable("Value"), listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("USA", "FR", "RU"))
            .put(Variable("found name"), listOf("United States of America", "France", "Russia"))
            .put(Variable("geometry"), listOf("usa_geometry", "fr_geometry", "ru_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Country"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerieFrom(data, "Country")
            .hasSerieFrom(data, "Category")
            .hasSerieFrom(data, "Value")
            .hasSerie(variable(map, "request"), listOf("USA", "USA", "RU", "RU", "FR", "FR"))
            .hasSerie(variable(map, "found name"), listOf("United States of America", "United States of America", "Russia", "Russia", "France", "France"))
            .hasSerie(variable(map, "geometry"), listOf("usa_geometry", "usa_geometry", "ru_geometry", "ru_geometry", "fr_geometry", "fr_geometry"))
    }

    @Test
    fun multiIndex_singleKey_ExtraMapEntries() {
        // Data: [USA, RU, FR]
        // Map: [GER, USA, FR, RU]
        // Result: [USA, RU, FR, GER]
        val data = DataFrame.Builder()
            .put(Variable("Country"), listOf("USA", "USA", "RU", "RU", "FR", "FR"))
            .put(Variable("Category"), listOf("A", "B", "A", "B", "A", "B"))
            .put(Variable("Value"), listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("GER", "USA", "FR", "RU"))
            .put(Variable("found name"), listOf("Germany", "United States of America", "France", "Russia"))
            .put(Variable("geometry"), listOf("ger_geometry", "usa_geometry", "fr_geometry", "ru_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Country"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Country"), listOf("USA", "USA", "RU", "RU", "FR", "FR", null))
            .hasSerie(variable(data, "Category"), listOf("A", "B", "A", "B", "A", "B", null))
            .hasSerie(variable(data, "Value"), listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, null))
            .hasSerie(variable(map, "request"), listOf("USA", "USA", "RU", "RU", "FR", "FR", "GER"))
            .hasSerie(variable(map, "found name"), listOf("United States of America", "United States of America", "Russia", "Russia", "France", "France", "Germany"))
            .hasSerie(variable(map, "geometry"), listOf("usa_geometry", "usa_geometry", "ru_geometry", "ru_geometry", "fr_geometry", "fr_geometry", "ger_geometry"))
    }


    @Test
    fun multiIndex_singleKey_MisingDataEntries() {
        // Remove data rows that not matched to a map

        // Data: [USA, RU, FR]
        // Map: [GER, USA, FR]
        // Result: [USA, FR, GER]
        val data = DataFrame.Builder()
            .put(Variable("Country"), listOf("USA", "USA", "RU", "RU", "FR", "FR"))
            .put(Variable("Category"), listOf("A", "B", "A", "B", "A", "B"))
            .put(Variable("Value"), listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("GER", "USA", "FR"))
            .put(Variable("found name"), listOf("Germany", "United States of America", "France"))
            .put(Variable("geometry"), listOf("ger_geometry", "usa_geometry", "fr_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Country"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Country"), listOf(
                "USA", "USA",
                "FR", "FR",
                null // GER
            ))
            .hasSerie(variable(data, "Category"), listOf(
                "A", "B", // USA
                "A", "B", // FR
                null // GER
            ))
            .hasSerie(variable(data, "Value"), listOf(
                0.0, 1.0, // USA
                4.0, 5.0, // FR
                null // GER
            ))
            .hasSerie(variable(map, "request"), listOf(
                "USA", "USA",
                "FR", "FR",
                "GER"
            ))
            .hasSerie(variable(map, "found name"), listOf(
                "United States of America", "United States of America",
                "France", "France",
                "Germany"
            ))
            .hasSerie(variable(map, "geometry"), listOf(
                "usa_geometry", "usa_geometry",
                "fr_geometry", "fr_geometry",
                "ger_geometry"
            ))
    }

    @Test
    fun multiIndex_singleKey_DuplicatedMapEntries() {
        // Remove data rows that not matched to a map

        // Data: [USA, RU, FR]
        // Map: [GER, FR, USA, FR]
        // Result: [USA, FR, GER]
        val data = DataFrame.Builder()
            .put(Variable("Country"), listOf("USA", "USA", "RU", "RU", "FR", "FR"))
            .put(Variable("Category"), listOf("A", "B", "A", "B", "A", "B"))
            .put(Variable("Value"), listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("GER", "FR", "USA", "FR"))
            .put(Variable("found name"), listOf("Germany", "France", "United States of America", "France"))
            .put(Variable("geometry"), listOf("ger_geometry", "fr_geometry", "usa_geometry", "fr_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Country"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Country"), listOf(
                "USA", "USA",
                "FR", "FR",
                null // GER
            ))
            .hasSerie(variable(data, "Category"), listOf(
                "A", "B", // USA
                "A", "B", // FR
                null // GER
            ))
            .hasSerie(variable(data, "Value"), listOf(
                0.0, 1.0, // USA
                4.0, 5.0, // FR
                null // GER
            ))
            .hasSerie(variable(map, "request"), listOf(
                "USA", "USA",
                "FR", "FR",
                "GER"
            ))
            .hasSerie(variable(map, "found name"), listOf(
                "United States of America", "United States of America",
                "France", "France",
                "Germany"
            ))
            .hasSerie(variable(map, "geometry"), listOf(
                "usa_geometry", "usa_geometry",
                "fr_geometry", "fr_geometry",
                "ger_geometry"
            ))
    }


    @Test
    fun multiIndex_singleKey_DuplicatedMapEntriesNotMatchingToData() {
        // Duplication in both data and map - map duplications will be removed

        // Data: [USA, RU, FR]
        // Map: [GER, FR, GER, USA]
        // Result: [USA, FR, GER]
        val data = DataFrame.Builder()
            .put(Variable("Country"), listOf("USA", "USA", "RU", "RU", "FR", "FR"))
            .put(Variable("Category"), listOf("A", "B", "A", "B", "A", "B"))
            .put(Variable("Value"), listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("GER", "FR", "GER", "USA"))
            .put(Variable("found name"), listOf("Germany", "France", "Germany", "United States of America"))
            .put(Variable("geometry"), listOf("ger_geometry", "fr_geometry", "ger_geometry", "usa_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("Country"), map, listOf("request"))

        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Country"), listOf(
                "USA", "USA",
                "FR", "FR",
                null // GER
            ))
            .hasSerie(variable(data, "Category"), listOf(
                "A", "B", // USA
                "A", "B", // FR
                null // GER
            ))
            .hasSerie(variable(data, "Value"), listOf(
                0.0, 1.0, // USA
                4.0, 5.0, // FR
                null // GER
            ))
            .hasSerie(variable(map, "request"), listOf(
                "USA", "USA",
                "FR", "FR",
                "GER"
            ))
            .hasSerie(variable(map, "found name"), listOf(
                "United States of America", "United States of America",
                "France", "France",
                "Germany"
            ))
            .hasSerie(variable(map, "geometry"), listOf(
                "usa_geometry", "usa_geometry",
                "fr_geometry", "fr_geometry",
                "ger_geometry"
            ))
    }


    class DataFrameAssert(actual: DataFrame?) :
        AbstractAssert<DataFrameAssert, DataFrame>(actual, DataFrameAssert::class.java) {

        fun hasVariables(vararg names: String): DataFrameAssert {
            Assertions.assertThat(actual.variables().map(Variable::name))
                .containsExactlyInAnyOrder(*names)
            return this
        }

        fun hasVariables(vararg variables: Variable): DataFrameAssert {
            Assertions.assertThat(actual.variables())
                .containsExactlyInAnyOrder(*variables)
            return this
        }

        fun hasSerie(variable: Variable, values: List<*>): DataFrameAssert {
            Assertions.assertThat(actual.get(variable))
                .containsExactlyElementsOf(values)
            return this
        }

        fun hasSerieFrom(df: DataFrame, name: String): DataFrameAssert {
            hasSerie(variable(df, name), values(df, name))
            return this
        }
    }

    private fun assertThat(df: DataFrame): DataFrameAssert {
        return DataFrameAssert(df)
    }

}
