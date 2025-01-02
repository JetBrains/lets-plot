## Publishing SNAPSHOT versions of JVM artifacts.

### 1. Prepare for publishing:

 - Check `version` in the root `build.gradle.kts` file: version must be like `X.X.X-SNAPSHOT`.
 - Add token to the `sonatype` section of the `local.properties` file.

### 2. Publish:

Run tne next gradle tasks from the project root:

```shell
./gradlew publishLetsPlotJvmCommonPublicationToMavenRepository \
          publishLetsPlotJvmJfxPublicationToMavenRepository \
          publishLetsPlotJvmBatikPublicationToMavenRepository \
          publishLetsPlotImageExportPublicationToMavenRepository \
          publishLetsPlotGISPublicationToMavenRepository \
          publishLetsPlotCoreModulesToMavenRepository
```

### 3. Check uploaded artifacts:

Check uploaded artifacts here:

https://oss.sonatype.org/content/repositories/snapshots/org/jetbrains/lets-plot/
                
### 4. Use SNAPSHOT artifacts:

Add snapshots repository to the `repositories` section of the `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
    // SNAPSHOTS
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}
```

### 5. Snapshot vertsion of JS artifacts.
TBD