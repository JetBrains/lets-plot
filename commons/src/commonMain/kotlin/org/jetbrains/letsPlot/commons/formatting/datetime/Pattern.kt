/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

/**
 * Pattern for "strftime" datetime formatting
 * (https://docs.python.org/3/library/datetime.html#strftime-strptime-behavior, www.strfti.me/)
 */
enum class Pattern(val string: String, val kind: Kind) {
    //Date
    DAY_OF_WEEK_ABBR("%a", Kind.DATE),
    DAY_OF_WEEK_FULL("%A", Kind.DATE),
    MONTH_ABBR("%b", Kind.DATE),
    MONTH_FULL("%B", Kind.DATE),
    DAY_OF_MONTH_LEADING_ZERO("%d", Kind.DATE),
    DAY_OF_MONTH("%e", Kind.DATE),
    DAY_OF_THE_YEAR("%j", Kind.DATE),
    MONTH("%m", Kind.DATE),

    //WEEK_NUMBER_FROM_SUNDAY("%U", Kind.DATE),
    //WEEK_NUMBER_FROM_MONDAY("%W", Kind.DATE),
    DAY_OF_WEEK("%w", Kind.DATE),

    //LOCALE_DATE("%x", Kind.DATE),
    YEAR_SHORT("%y", Kind.DATE),
    YEAR_FULL("%Y", Kind.DATE),

    //Time
    HOUR_24("%H", Kind.TIME),
    HOUR_12_LEADING_ZERO("%I", Kind.TIME),
    HOUR_12("%l", Kind.TIME),
    MINUTE("%M", Kind.TIME),
    MERIDIAN_LOWER("%P", Kind.TIME),
    MERIDIAN_UPPER("%p", Kind.TIME),
    SECOND("%S", Kind.TIME);
    //LOCALE_TIME("%X", Kind.TIME),
    //TIME_ZONE("%Z", Kind.TIME),

    //Other
    //LOCALE_DATE_TIME("%c", Kind.OTHER);


    companion object {
        val PATTERN_REGEX = "(%[aAbBdejmwyYHIlMpPS])".toRegex()

        enum class Kind {
            DATE,
            TIME
        }

        fun patternByString(patternString: String) = Pattern.entries.find { it.string == patternString }

        fun isDateTimeFormat(patternString: String) = PATTERN_REGEX.containsMatchIn(patternString)
    }
}