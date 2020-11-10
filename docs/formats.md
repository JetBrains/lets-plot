#Formatting

Formatting provides the ability to do complex variable substitutions and value formatting.


- [Number format](#number-format)
    - [Examples](#examples-number-format)
- [String template](#string-template)  
- [Date and time format](#datetime)
    - [Examples](#examples-datetime)
------
<a id="number-format"></a>
##Number format
Provides a wide variety of formats for numbers.

Class `NumberFormat` implements number format: `NumberFormat(spec: String)`, where `spec` - string format specifier.

Function `apply(num: Number): String`, where `num` - number for formatting, returns the formatted number.

The general form of a specifier is:

```
[​[fill]align][sign][symbol][0][width][,][.precision][type]
```

*`fill`* - can be any character, defaults to a space if omitted. The presence of a fill character is signaled by the `*align*` character following it,
which must be one of the alignment options.

*`align`* - the various alignment options is as follows:

* `>` - forces the field to be right-aligned within the available space (default behavior);
* `<` - forces the field to be left-aligned within the available space;
* `^` - forces the field to be centered within the available space;
* `=` - like `>`, but with any sign and symbol to the left of any padding;

*`sign`* can be:

* `-` - nothing for zero or positive and a minus sign for negative (default behavior);
* `+` - a plus sign for zero or positive and a minus sign for negative;
* ` ` (space) - a space for zero or positive and a minus sign for negative.

*`symbol`* can be:

* `$` - apply currency symbols per the locale definition;
* `#` - for binary, octal, or hexadecimal notation, prefix by `0b`, `0o`, or `0x`, respectively.

*`zero`* (`0`) option enables zero-padding; this implicitly sets *fill* to `0` and *align* to `=`.

*`width`* defines the minimum field width; if not specified, then the width will be determined by the content.

*`comma`* (`,`) option enables the use of a group separator, such as a comma for thousands.

*`precision`* depending on the *`type`*, the *`precision`* either indicates the number of digits that follow the decimal point (types `f` and `%`), or the number of significant digits (types` ​`, `e`, `g`, `r`, `s` and `p`).
If the precision is not specified, it defaults to 6 for all types except ​ (none), which defaults to 12.
Precision is ignored for integer formats (types `b`, `o`, `d`, `x`, `X` and `c`).

*`type`* determines how the data should be presented:

* `e` - exponent notation;
* `f` - fixed point notation;
* `g` - either decimal or exponent notation, rounded to significant digits;
* `s` - decimal notation with an SI prefix, rounded to significant digits;
* `%` - multiply by 100, and then decimal notation with a percent sign;
* `b` - binary notation, rounded to integer;
* `o` - octal notation, rounded to integer;
* `d` - decimal notation, rounded to integer;
* `x` - hexadecimal notation, using lower-case letters, rounded to integer;
* `X` - hexadecimal notation, using upper-case letters, rounded to integer;
* `c` - simple toString.

The following SI prefixes are supported for `s` type:

* `y` - yocto, 10⁻²⁴
* `z` - zepto, 10⁻²¹
* `a` - atto, 10⁻¹⁸
* `f` - femto, 10⁻¹⁵
* `p` - pico, 10⁻¹²
* `n` - nano, 10⁻⁹
* `µ` - micro, 10⁻⁶
* `m` - milli, 10⁻³
* `​` (none) - 10⁰
* `k` - kilo, 10³
* `M` - mega, 10⁶
* `G` - giga, 10⁹
* `T` - tera, 10¹²
* `P` - peta, 10¹⁵
* `E` - exa, 10¹⁸
* `Z` - zetta, 10²¹
* `Y` - yotta, 10²⁴



<a id="examples-number-format"></a>
###Examples

Apply NumberFormat to `number = 42`:
```
08d      -->  "00000042"
_<8d     -->  "______42"
_=8d     -->  "___42___"
_=+8d    -->  "+_____42"
_^11.0%  -->  "____42%____"
_^11,.0% -->  "__42,200%__"
+08,d    -->  "+0,000,042"
.1f      -->  "42.0"
+.3f     -->  "+42.000"
b        -->  "101010"
#b       -->  "0b101010"
o        -->  "52"
e        -->  "4.200000e+1"
s        -->  "42.0000"
020,s    -->  "000,000,000,042.0000"
020.0%   -->  "0000000000000004200%"
```
```
NumberFormat(".1f").apply(0.42) --> "0.4" 
NumberFormat("10,.2f").apply(1234567.449) --> "1,234,567.45"
NumberFormat("+$,.2f").apply(1e4) --> "+$10,000.00"
```

<a id="string-template"></a>
##String template

The number format can be used in a template to create a string with variable substitution.
The string template contains “replacement fields” surrounded by curly braces `{}`. 
Anything that is not contained in braces is considered literal text, which is copied unchanged to the result string. 
If you need to include a brace character in the literal text, it can be escaped by doubling: {{ and }}.
This approach is used in function `layer_tooltips()` to customize the content of tooltips.

See: [Tooltip Customization in Lets-Plot](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md).


<a id="datetime"></a>
##Date and time format
Provides formats for date and time values.


Class `DateTime` implements the datetime format:

`DateTime(date: Date, time: Time)`

* `Date` has attributes: `year`, `month` and `day`;
* `Time` has attributes: `hours`, `minutes`, `seconds` and `milliseconds`.

Class `Format` implements a date/time formatting: `Format(spec: String)`, where `spec` - string format specifier.

Functions to get formatted value:
`apply(dateTime: DateTime): String`, 
`apply(date: Date): String`,
`apply(time: Time): String`.

The list of supported directives to format date/time values:
* `%a`- weekday as an abbreviated name (Sun, Mon, …, Sat);
* `%A` - weekday as a full name (Sunday, Monday, …, Saturday)
* `%b`- month as an abbreviated name (Jan, Feb, …, Dec);
* `%B`- month as a full name (January, February, …, December);
* `%d` - day of the month as a zero-padded decimal number (01, 02, …, 31);
* `%e` - day of the month as a decimal number (1, 2, …, 31);
* `%j` - day of the year as a zero-padded decimal number (001, 002, …, 366).
* `%m` - month as a zero-padded decimal number (01, 02, …, 12);
* `%w` - weekday as a decimal number, where 0 is Sunday and 6 is Saturday (0, 1, …, 6);
* `%y` - year without century as a zero-padded decimal number (00, 01, …, 99);
* `%Y` - year with century as a decimal number (0001, 0002, …, 2013, 2014, …, 9998, 9999);
* `%H` - hour (24-hour clock) as a zero-padded decimal number (00, 01, …, 23);
* `%I` - hour (12-hour clock) as a zero-padded decimal number (01, 02, …, 12);
* `%l` - hour (12-hour clock) as a decimal number (1, 2, …, 12);
* `%M` - minute as a zero-padded decimal number (00, 01, …, 59);
* `%p` - "AM" or "PM" according to the given time value;
* `%P` - like %p but in lowercase: "am" or "pm";
* `%S` - second as a zero-padded decimal number (00, 01, …, 59).

<a id="examples-datetime"></a>
###Examples


Apply format to `DateTime(date, time)`, 
where `date=Date(6, Month.AUGUST, 2019)`, `time=Time(4, 46, 35)`
```
%a  --> "Tue"
%A  --> "Tuesday"
%b  --> "Aug"
%B  --> "August"
%d  --> "06"
%e  --> "6"
%j  --> "218"
%m  --> "08"
%w  --> "2" 
%y  --> "19"
%Y  --> "2019"
%H  --> "04"
%I  --> "04"
%l  --> "4"
%M  --> "46"
%P  --> "am"
%p  --> "AM"
%S  --> "35"
```
```
%Y-%m-%dT%H:%M:%S            --> "2019-08-06T04:46:35
----!%%%YY%md%dT%H:%M:%S%%%  -->  "----!%%2019Y08d06T04:46:35%%%"
```