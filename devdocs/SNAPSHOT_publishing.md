## Publishing SNAPSHOT versions of JVM artifacts.

### Publishing to Local Maven Repository

> **Note**: our custom local Maven repository location is `<project root>/.maven-publish-dev-repo`.

> **Note**: make sure that **version** is set to "0.0.0-SNAPSHOT" in `build.gradle.kts`.

```shell
./gradlew publishLetsPlotJvmCommonPublicationToMavenLocalRepository
./gradlew publishLetsPlotJvmJfxPublicationToMavenLocalRepository
./gradlew publishLetsPlotJvmBatikPublicationToMavenLocalRepository
./gradlew publishLetsPlotImageExportPublicationToMavenLocalRepository
./gradlew publishLetsPlotGISPublicationToMavenLocalRepository
./gradlew publishLetsPlotCoreModulesToMavenLocalRepository
./gradlew publishLetsPlotIdeaPluginPublicationToMavenLocalRepository

```

### Publishing to Sonatype Maven Repository

#### Credentials

In the `local.properties` file:
```properties
sonatype.username=<your Sonatype username>
sonatype.password=<your Sonatype access token>
sonatype.profileID=<your Sonatype profile ID>
```

#### SNAPSHOT Version

Make sure the `version` in the root `build.gradle.kts` file is a SNAPSHOT version: `X.X.X-SNAPSHOT`.

#### Publish

```shell
./gradlew publishLetsPlotJvmCommonPublicationToMavenRepository \
          publishLetsPlotJvmJfxPublicationToMavenRepository \
          publishLetsPlotJvmBatikPublicationToMavenRepository \
          publishLetsPlotImageExportPublicationToMavenRepository \
          publishLetsPlotGISPublicationToMavenRepository \
          publishLetsPlotCoreModulesToMavenRepository \
          publishLetsPlotIdeaPluginPublicationToMavenRepository
```

#### Check Uploaded Artifacts

Check uploaded artifacts here:

https://oss.sonatype.org/content/repositories/snapshots/org/jetbrains/lets-plot/
                
#### Using SNAPSHOT artifacts

Add snapshots repository to the `repositories` section of the `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
    // SNAPSHOTS
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}
```

#### Snapshot Vertsion of JS Artifacts
ToDo.