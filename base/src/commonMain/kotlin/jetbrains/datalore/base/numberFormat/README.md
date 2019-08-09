# Number Format reference

## Format(spec: String)

Class that implements number format.

`spec` - string format specifier.

The general form of a specifier is:

```
[​[fill]align][sign][symbol][0][width][,][.precision][type]
```

*fill* - can be any character. The presence of a fill character is signaled by the *align* character following it.

*align* - can be:

* `>` - Forces the field to be right-aligned within the available space. (Default behavior).
* `<` - Forces the field to be left-aligned within the available space.
* `^` - Forces the field to be centered within the available space.
* `=` - like `>`, but with any sign and symbol to the left of any padding.

*sign* can be:

* `-` - nothing for zero or positive and a minus sign for negative. (Default behavior.)
* `+` - a plus sign for zero or positive and a minus sign for negative.
* ` ` (space) - a space for zero or positive and a minus sign for negative.

*symbol* can be:

* `$` - apply currency symbols per the locale definition.
* `#` - for binary, octal, or hexadecimal notation, prefix by `0b`, `0o`, or `0x`, respectively.

*zero* (`0`) option enables zero-padding; this implicitly sets *fill* to `0` and *align* to `=`.

*width* defines the minimum field width; if not specified, then the width will be determined by the content.

*comma* (`,`) option enables the use of a group separator, such as a comma for thousands.

*precision* Depending on the *type*, the *precision* either indicates the number of digits that follow the decimal point (types `f` and `%`), or the number of significant digits (types` ​`, `e`, `g`, `r`, `s` and `p`).
If the precision is not specified, it defaults to 6 for all types except ​ (none), which defaults to 12.
Precision is ignored for integer formats (types `b`, `o`, `d`, `x`, `X` and `c`).

*type* can be:

* `e` - exponent notation.
* `f` - fixed point notation.
* `g` - either decimal or exponent notation, rounded to significant digits.
* `%` - multiply by 100, and then decimal notation with a percent sign.
* `b` - binary notation, rounded to integer.
* `o` - octal notation, rounded to integer.
* `d` - decimal notation, rounded to integer.
* `x` - hexadecimal notation, using lower-case letters, rounded to integer.
* `X` - hexadecimal notation, using upper-case letters, rounded to integer.
* `c` - simple toString.

Format spec compatible with [Python Format Specification Mini-Language](https://docs.python.org/3/library/string.html#format-specification-mini-language) except `s`, `n`, `E`, `F` and `G` types.

## apply(num: Number): String

`num` - number for formatting.

Returns formatted number.