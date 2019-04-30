package jetbrains.datalore.base.numberFormat

import java.text.DecimalFormat

actual object NumberFormatUtil {

    actual fun formatNumber(num: Number, pattern: String): String {
        if (num is Double && num.isNaN()) return "NaN"
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
}
