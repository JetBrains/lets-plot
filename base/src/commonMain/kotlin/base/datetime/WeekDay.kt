package jetbrains.datalore.base.datetime

enum class WeekDay private constructor(val abbreviation: String, val isWeekend: Boolean) {
    MONDAY("MO", false),
    TUESDAY("TU", false),
    WEDNESDAY("WE", false),
    THURSDAY("TH", false),
    FRIDAY("FR", false),
    SATURDAY("SA", true),
    SUNDAY("SU", true)
}
