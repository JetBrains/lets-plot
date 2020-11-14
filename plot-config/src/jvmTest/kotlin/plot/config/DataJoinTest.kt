/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
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

        // Should take variables from corresponding dataframes, not recreate them
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

        // Should take variables from corresponding dataframes, not recreate them
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

        // Should take variables from corresponding dataframes, not recreate them
        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Countries"), listOf(null, "USA", null, "FR", "RU")) // nulls for UA and GER
            .hasSerie(variable(data, "Values"), listOf(null, 0.0, null, 2.0, 1.0)) // nulls for UA and GER
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
    }


    @Test
    fun tripleKey_extraMapRows() {
        // User searches names from data - same size, same order
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

        // Should take variables from corresponding dataframes, not recreate them
        assertThat(jointDataFrame)
            .hasSerie(variable(data, "Countries"), listOf(null, "USA", "USA", null, "USA"))
            .hasSerie(variable(data, "States"), listOf(null, "TX", "AL", null, "CA"))
            .hasSerie(variable(data, "Counties"), listOf(null, "Anderson", "Clay", null, "Alameda"))
            .hasSerie(variable(data, "Values"), listOf(null, 0.0, 1.0, null, 2.0))
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "state")
            .hasSerieFrom(map, "country")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
    }


    @Test
    fun singleKey_extraDataRows() {
        // Like drop_not_matched() from geocoding
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
            .hasSerie(variable(data, "Countries"), listOf("FR", "RU"))
            .hasSerie(variable(data, "Values"), listOf(2.0, 1.0))
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
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
    @Ignore("ToDo: fix later")
    fun singleKey_DupsInData() {
        val data = DataFrame.Builder()
            .put(Variable("state"), listOf(
                "AL", "AL",
                "CO", "CO", "CO",
                "IL", "IL", "IL", "IL"
            ))
            .put(Variable("item"), listOf(
                "State Debt", "Local Debt", "Gross State Product",
                "State Debt", "Local Debt", "Gross State Product",
                "State Debt", "Local Debt", "Gross State Product"
            ))
            .put(Variable("value"), listOf(
                10.7, 26.1, 228.0,
                5.9, 3.5, 55.7,
                13.3, 30.5, 361.1
            ))
            .build()

        val map = DataFrame.Builder()
            .put(Variable("request"), listOf("AL", "CO", "IL"))
            .put(Variable("found name"), listOf("Alabama", "Colorado", "Illinois"))
            .put(Variable("geometry"), listOf("al_geometry", "co_geometry", "il_geometry"))
            .build()

        val jointDataFrame = ConfigUtil.join(data, listOf("state"), map, listOf("request"))
        assertThat(jointDataFrame)
            .hasSerieFrom(data, "state")
            .hasSerieFrom(data, "item")
            .hasSerieFrom(data, "value")
            .hasSerieFrom(map, "request")
            .hasSerieFrom(map, "found name")
            .hasSerieFrom(map, "geometry")
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
