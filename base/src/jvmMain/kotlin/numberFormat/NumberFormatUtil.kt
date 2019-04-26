package jetbrains.datalore.base.numberFormat

import java.text.DecimalFormat

object NumberFormatUtil {

    fun formatNumber(num: Number, pattern: String): String {
        if (num is Double && java.lang.Double.isNaN(num)) return "NaN"
        // We have 'super-source' partial implementation of java.text.DecimalFormat
        // which is based on com.google.gwt.i18n.client.NumberFormat
        // and can be used on client side.
        var s = DecimalFormat(pattern).format(num.toDouble())

        if (s.matches("-[^123456789]+(([eE].+)|$)".toRegex())) {
            // matches negative zero pattern (-0.000, -0.00E3)
            s = s.substring(1)
        }
        return s
    }

    //  def decimal pattern    :#,##0.###
    //  def scientific pattern :#E0
    //  def currency pattern :¤#,##0.00;(¤#,##0.00)
    //  def percent pattern :#,##0%
    //  def decimal pattern    :7,500.25
    //  def scientific pattern :8E3
    //  def currency pattern :US$7,500.25
    //  def percent pattern :750,025%

}
