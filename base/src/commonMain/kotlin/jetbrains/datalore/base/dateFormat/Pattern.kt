package jetbrains.datalore.base.dateFormat

/**
 * Pattern for "strftime" datetime formatting
 * (https://docs.python.org/3/library/datetime.html#strftime-strptime-behavior, http://www.strfti.me/)
 */
enum class Pattern(val string: String) {
    //Date
    DAY_OF_WEEK_ABBR("%a"),
    DAY_OF_WEEK_FULL("%A"),
    MONTH_ABBR("%b"),
    MONTH_FULL("%B"),
    DAY_OF_MONTH_LEADING_ZERO("%d"),
    DAY_OF_MONTH("%e"),
    DAY_OF_THE_YEAR("%j"),
    MONTH("%m"),
    //WEEK_NUMBER_FROM_SUNDAY("%U"),
    //WEEK_NUMBER_FROM_MONDAY("%W"),
    DAY_OF_WEEK("%w"),
    //LOCALE_DATE("%x"),
    YEAR_SHORT("%y"),
    YEAR_FULL("%Y"),

    //Time
    HOUR_24("%H"),
    HOUR_12_LEADING_ZERO("%I"),
    HOUR_12("%l"),
    MINUTE("%M"),
    MERIDIAN_LOWER("%P"),
    MERIDIAN_UPPER("%p"),
    SECOND("%S");
    //LOCALE_TIME("%X"),
    //TIME_ZONE("%Z"),

    //Other
    //LOCALE_DATE_TIME("%c");


    companion object {
        val PATTERN_REGEX = "(%[aAbBdejmwyYHIlMpPS])".toRegex()

        fun patternByString(patternString: String) = values().find { it.string == patternString }
    }
}