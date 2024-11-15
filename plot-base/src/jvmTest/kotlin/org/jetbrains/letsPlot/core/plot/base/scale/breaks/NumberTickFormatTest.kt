/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberTickFormatTest {

//    @Rule
//    var exception = ExpectedException.none()

    @BeforeTest
    fun setUSAsDefaultLocale() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun normal() {
        assertEquals(
            "50",
            format(50.1, 100.0, 5.0)
        )
        assertEquals(
            "50",
            format(49.9, 100.0, 5.0)
        )

        assertEquals(
            "50",
            format(50.1, 100.0, 50.0)
        )
        assertEquals(
            "50",
            format(49.9, 100.0, 50.0)
        )

        assertEquals(
            "50",
            format(50.1, 100.0, 200.0)
        )
        assertEquals(
            "50",
            format(49.9, 100.0, 200.0)
        )

        assertEquals(
            "50",
            format(50.01, 100.0, 0.5)
        )
        assertEquals(
            "50.1",
            format(50.1, 100.0, 0.5)
        )
        assertEquals(
            "49.9",
            format(49.9, 100.0, 0.5)
        )
        assertEquals(
            "50",
            format(49.99, 100.0, 0.5)
        )

        assertEquals(
            "50.01",
            format(50.01, 100.0, 0.05)
        )
        assertEquals(
            "50.1",
            format(50.1, 100.0, 0.05)
        )
        assertEquals(
            "49.9",
            format(49.9, 100.0, 0.05)
        )
        assertEquals(
            "49.99",
            format(49.99, 100.0, 0.05)
        )
    }

    @Test
    fun step_small() {
        val domainAndStep = doubleArrayOf(100.0, 0.0000005)
        assertEquals(
            "50.1",
            format(50.1, domainAndStep)
        )
        assertEquals(
            "50.0000005",
            format(50.0000005, domainAndStep)
        )
        assertEquals(
            "49.9999999",
            format(49.9999999, domainAndStep)
        )
        assertEquals(
            "49.9999999",
            format(49.99999991, domainAndStep)
        )
        assertEquals(
            "50",
            format(49.99999999, domainAndStep)
        )
    }

    @Test
    fun step_ultraSmall() {
        val domainAndStep = doubleArrayOf(100.0, 5e-10)
        assertEquals(
            "50.1",
            format(50.1, domainAndStep)
        )
        assertEquals(
            "50.1000000005",
            format(50.1 + 5e-10, domainAndStep)
        )
        assertEquals(
            "49.9999999999",
            format(50 - 1e-10, domainAndStep)
        )
        assertEquals(
            "50",
            format(50 - 1e-11, domainAndStep)
        )
    }

    @Test
    fun both_small() {
        val domainAndStep = doubleArrayOf(0.01, 0.0005)
        assertEquals(
            "0.005",
            format(0.005, domainAndStep)
        )
        assertEquals(
            "0.0055",
            format(0.0055, domainAndStep)
        )
        assertEquals(
            "0.005",
            format(
                0.00499999999,
                domainAndStep
            )
        )
    }

    @Test
    fun both_ultraSmall() {
        val domainAndStep = doubleArrayOf(1e-7, 5e-10)

        assertEquals("5·\\(10^{-8}\\)", format(.00000005, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("5.05·\\(10^{-8}\\)", format(.00000005 + 5e-10, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("1.505·\\(10^{-7}\\)", format(.00000015 + 5e-10, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))

        assertEquals("5·\\(10^{-8}\\)", format(.00000005, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("5.05·\\(10^{-8}\\)", format(.00000005 + 5e-10, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("1.505·\\(10^{-7}\\)", format(.00000015 + 5e-10, domainAndStep, ExponentFormat(ExponentNotationType.POW)))

        assertEquals("5e-8", format(.00000005, domainAndStep, ExponentFormat(ExponentNotationType.E)))
        assertEquals("5.05e-8", format(.00000005 + 5e-10, domainAndStep, ExponentFormat(ExponentNotationType.E)))
        assertEquals("1.505e-7", format(.00000015 + 5e-10, domainAndStep, ExponentFormat(ExponentNotationType.E)))
    }

    @Test
    fun domain_large() {
        val domainAndStep = doubleArrayOf(10000.0, 5.0)
        assertEquals(
            "5,000",
            format(5000, domainAndStep)
        )
        assertEquals(
            "5,000",
            format(5000.1, domainAndStep)
        )
        assertEquals(
            "5,001",
            format(5001.1, domainAndStep)
        )
    }

    @Test
    fun domain_larger() {
        val domainAndStep = doubleArrayOf(1e7, 5.0)
        assertEquals("5,000,000", format(5e6, domainAndStep))
        assertEquals("5,000,005", format(5e6 + 5, domainAndStep))

        assertEquals("5,000,000", format(5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("5,000,005", format(5e6 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW)))

        assertEquals("5,000,000", format(5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("5,000,005", format(5e6 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
    }

    @Test
    fun domain_ultraLarge() {
        val domainAndStep = doubleArrayOf(1e8, 5.0)
        assertEquals("5e+7", format(5e7, domainAndStep))
        assertEquals("5.0000005e+7", format(5e7 + 5, domainAndStep))

        assertEquals("5·\\(10^{7}\\)", format(5e7, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("5.0000005·\\(10^{7}\\)", format(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW)))

        assertEquals("5·\\(10^{7}\\)", format(5e7, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("5.0000005·\\(10^{7}\\)", format(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
    }

    @Test
    fun both_ultraLarge_metricPrefix() {
        val domainAndStep = doubleArrayOf(1e8, 5e6)
        assertEquals("1.05e+8", format(1e8 + 5e6, domainAndStep))
        assertEquals("5e+7", format(5e7, domainAndStep))
        assertEquals("5e+7", format(5e7 + 5, domainAndStep))
        assertEquals( "5.5e+7", format(5e7 + 5e6, domainAndStep))

        assertEquals("5·\\(10^{7}\\)", format(5e7, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("5·\\(10^{7}\\)", format(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals( "5.5·\\(10^{7}\\)", format(5e7 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("1.05·\\(10^{8}\\)", format(1e8 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW)))

        assertEquals("5·\\(10^{7}\\)", format(5e7, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("5·\\(10^{7}\\)", format(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals( "5.5·\\(10^{7}\\)", format(5e7 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("1.05·\\(10^{8}\\)", format(1e8 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
    }

    @Test
    fun both_ultraLarge_scientific() {
        val domainAndStep = doubleArrayOf(1e8, 5e6)

        assertEquals("5·\\(10^{7}\\)", formatScientific(5e7, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("5·\\(10^{7}\\)", formatScientific(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("5.5·\\(10^{7}\\)", formatScientific(5e7 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))
        assertEquals("1.05·\\(10^{8}\\)", formatScientific(1e8 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW_FULL)))

        assertEquals("5·\\(10^{7}\\)", formatScientific(5e7, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("5·\\(10^{7}\\)", formatScientific(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("5.5·\\(10^{7}\\)", formatScientific(5e7 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW)))
        assertEquals("1.05·\\(10^{8}\\)", formatScientific(1e8 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.POW)))

        assertEquals("5e+7", formatScientific(5e7, domainAndStep, ExponentFormat(ExponentNotationType.E)))
        assertEquals("5e+7", formatScientific(5e7 + 5, domainAndStep, ExponentFormat(ExponentNotationType.E)))
        assertEquals("5.5e+7", formatScientific(5e7 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.E)))
        assertEquals("1.05e+8", formatScientific(1e8 + 5e6, domainAndStep, ExponentFormat(ExponentNotationType.E)))
    }

    @Test
    fun zeroDomainRepresentative() {
//        exception.expect(IllegalArgumentException.class);
//        exception.expectMessage("Domain representative value can't be 0");
        assertEquals(
            "50",
            format(50.1, 0.0, 10.0)
        )
    }

    @Test
    fun zeroStep() {
//        exception.expect(IllegalArgumentException.class);
//        exception.expectMessage("Step can't be 0");
        assertEquals(
            "50",
            format(50.1, 100.0, 0.0)
        )
    }

    companion object {
        private fun format(number: Number, domainAndStep: DoubleArray, expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.E)): String {
            return format(
                number,
                domainAndStep[0],
                domainAndStep[1],
                expFormat
            )
        }

        private fun format(number: Number, domain: Double, step: Double, expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.E)): String {
            val formatter = NumericBreakFormatter(
                domain,
                step,
                allowMetricPrefix = true,
                expFormat = expFormat
            )
            return formatter.apply(number)
        }

        private fun formatScientific(number: Number, domainAndStep: DoubleArray, expFormat: ExponentFormat): String {
            val formatter = NumericBreakFormatter(
                domainAndStep[0],
                domainAndStep[1],
                allowMetricPrefix = false,
                expFormat = expFormat
            )
            return formatter.apply(number)
        }

    }
}
