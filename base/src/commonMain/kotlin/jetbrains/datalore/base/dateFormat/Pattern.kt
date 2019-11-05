/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.dateFormat

/**
 * Pattern for "strftime" datetime formatting
 * (https://docs.python.org/3/library/datetime.html#strftime-strptime-behavior, http://www.strfti.me/)
 */
enum class Pattern(val string: String, val type: Int) {
    //Date
    DAY_OF_WEEK_ABBR("%a", Pattern.DATE_TYPE),
    DAY_OF_WEEK_FULL("%A", Pattern.DATE_TYPE),
    MONTH_ABBR("%b", Pattern.DATE_TYPE),
    MONTH_FULL("%B", Pattern.DATE_TYPE),
    DAY_OF_MONTH_LEADING_ZERO("%d", Pattern.DATE_TYPE),
    DAY_OF_MONTH("%e", Pattern.DATE_TYPE),
    DAY_OF_THE_YEAR("%j", Pattern.DATE_TYPE),
    MONTH("%m", Pattern.DATE_TYPE),
    //WEEK_NUMBER_FROM_SUNDAY("%U", Pattern.DATE_TYPE),
    //WEEK_NUMBER_FROM_MONDAY("%W", Pattern.DATE_TYPE),
    DAY_OF_WEEK("%w", Pattern.DATE_TYPE),
    //LOCALE_DATE("%x", Pattern.DATE_TYPE),
    YEAR_SHORT("%y", Pattern.DATE_TYPE),
    YEAR_FULL("%Y", Pattern.DATE_TYPE),

    //Time
    HOUR_24("%H", Pattern.TIME_TYPE),
    HOUR_12_LEADING_ZERO("%I", Pattern.TIME_TYPE),
    HOUR_12("%l", Pattern.TIME_TYPE),
    MINUTE("%M", Pattern.TIME_TYPE),
    MERIDIAN_LOWER("%P", Pattern.TIME_TYPE),
    MERIDIAN_UPPER("%p", Pattern.TIME_TYPE),
    SECOND("%S", Pattern.TIME_TYPE);
    //LOCALE_TIME("%X", Pattern.TIME_TYPE),
    //TIME_ZONE("%Z", Pattern.TIME_TYPE),

    //Other
    //LOCALE_DATE_TIME("%c", Pattern.OTHER_TYPE);


    companion object {
        val PATTERN_REGEX = "(%[aAbBdejmwyYHIlMpPS])".toRegex()

        const val DATE_TYPE: Int = 0
        const val TIME_TYPE: Int = 1
        const val OTHER_TYPE: Int = 2

        fun patternByString(patternString: String) = values().find { it.string == patternString }
    }
}