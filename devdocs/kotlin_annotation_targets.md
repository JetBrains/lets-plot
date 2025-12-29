# Kotlin Annotation Use-Site Targets

## Overview

When using Lets-Plot in Kotlin applications (especially command-line tools with frameworks like picocli), you may encounter unfamiliar annotation syntax like `@set:Option` or `@get:Rule`. These are **Kotlin annotation use-site targets**, a language feature that specifies exactly where an annotation should be applied.

## Understanding Annotation Use-Site Targets

In Kotlin, a single property declaration can generate multiple elements under the hood:
- A field
- A getter method
- A setter method (for `var` properties)
- The constructor parameter (if declared in the primary constructor)

When you apply an annotation to a property, Kotlin needs to know which of these elements the annotation should target.

## Common Use-Site Targets

### `@set:` - Target the Setter

The `@set:` target applies the annotation to the property's setter method.

**Example from MiXCR CommandPaExportPlots.kt:**
```kotlin
@set:Option(
    description = [CommonDescriptions.METADATA],
    names = ["--metadata"],
    paramLabel = "<path.tsv>",
    order = OptionsOrder.main + 4_000
)
var metadata: Path? = null
    set(value) {
        ValidationException.requireFileType(value, InputFileType.TSV)
        ValidationException.requireFileExists(value)
        field = value
    }
```

In this case, picocli's `@Option` annotation needs to be on the setter because:
1. The property has a custom setter with validation logic
2. Picocli needs to call the setter when processing command-line arguments
3. The annotation must target the setter specifically, not the field or getter

### `@get:` - Target the Getter

The `@get:` target applies the annotation to the property's getter method.

**Example from Lets-Plot test code:**
```kotlin
@get:Rule
var testName = TestName()
```

Here, JUnit's `@Rule` annotation must target the getter method, as JUnit looks for annotated methods to set up test rules.

### `@field:` - Target the Backing Field

The `@field:` target applies the annotation to the backing field.

**Example:**
```kotlin
@field:Transient
var tempData: String? = null
```

This is useful for serialization frameworks that need to mark fields as transient.

### `@param:` - Target the Constructor Parameter

The `@param:` target applies the annotation to the constructor parameter.

**Example:**
```kotlin
class PlotConfig(
    @param:JsonProperty("width")
    val plotWidth: Int
)
```

## Why Use-Site Targets Matter

### Default Behavior

When you write `@Option var metadata: Path?`, Kotlin needs to decide where to apply the annotation. Different frameworks expect annotations in different places:

- **picocli**: Often needs annotations on setters or fields
- **JUnit**: Needs annotations on getters for rules
- **Jackson/Gson**: May need annotations on fields or getters
- **JPA/Hibernate**: Typically needs annotations on fields or getters

### Explicit Targeting

By using annotation use-site targets like `@set:Option`, you explicitly tell Kotlin where to place the annotation, ensuring compatibility with the framework's expectations.

## Complete List of Use-Site Targets

- `@field:` - Applies to the backing field
- `@get:` - Applies to the property getter
- `@set:` - Applies to the property setter
- `@param:` - Applies to the constructor parameter
- `@property:` - Applies to the property itself (not available from Java)
- `@receiver:` - Applies to the receiver parameter of an extension function or property
- `@setparam:` - Applies to the setter parameter
- `@delegate:` - Applies to the field storing the delegate instance for a delegated property

## When to Use Annotation Use-Site Targets

You should explicitly specify a use-site target when:

1. **The framework requires it**: Some frameworks (like picocli with custom setters) need annotations on specific elements
2. **Multiple annotations apply**: When using multiple annotations that should target different elements
3. **Clarity and intent**: To make your code's intent explicit and prevent confusion
4. **Compiler warnings**: When the compiler warns about ambiguous annotation targets

## Examples with Lets-Plot

While Lets-Plot itself doesn't require special annotation targeting in most use cases, you might encounter these patterns when:

1. **Building CLI tools** that generate plots (like MiXCR does)
2. **Integrating with frameworks** that use annotations (Spring, picocli, etc.)
3. **Serializing plot configurations** with Jackson or similar libraries

**Example: CLI tool with plot export:**
```kotlin
import jetbrains.letsPlot.intern.Plot
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "export-plot")
class PlotExporter {
    @set:Option(
        names = ["--width"],
        description = ["Plot width in pixels"]
    )
    var width: Int = 800
        set(value) {
            require(value > 0) { "Width must be positive" }
            field = value
        }

    @Option(
        names = ["--output"],
        description = ["Output file path"]
    )
    lateinit var outputPath: String

    fun exportPlot(plot: Plot) {
        // Export logic using the configured width and output path
    }
}
```

## Further Reading

- [Kotlin Documentation: Annotations](https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets)
- [picocli Documentation](https://picocli.info/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)

## Summary

Annotation use-site targets (`@set:`, `@get:`, etc.) are a Kotlin language feature that allows precise control over where annotations are applied. They're not specific to Lets-Plot but may appear in code that uses Lets-Plot alongside other frameworks. Understanding these targets helps you work effectively with Kotlin in various contexts, including when building applications that generate or export plots.
