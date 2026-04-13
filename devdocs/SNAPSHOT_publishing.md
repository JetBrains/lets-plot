## Publishing SNAPSHOT artifacts.

### Publishing to Local Maven Repository

> **Note**: our custom local Maven repository location is `<project root>/.maven-publish-dev-repo`.

> **Note**: make sure that **version** is set to "0.0.0-SNAPSHOT" in `build.gradle.kts`.

```shell
./gradlew :jvm-package:jvm-publish-common:publishAllPublicationsToMavenLocalRepository \
          publishLetsPlotJvmSwingPublicationToMavenLocalRepository \
          publishLetsPlotJvmBatikPublicationToMavenLocalRepository \
          publishLetsPlotGISPublicationToMavenLocalRepository \
          publishLetsPlotCoreModulesToMavenLocalRepository \
          publishLetsPlotIdeaPluginPublicationToMavenLocalRepository

```

### Publishing to Sonatype Maven Repository

#### Credentials

In the `local.properties` file:
```properties
sonatype.username=<your Sonatype username>
sonatype.password=<your Sonatype access token>
```

#### SNAPSHOT Version

Make sure the `version` in the root `build.gradle.kts` file is a SNAPSHOT version: `X.X.X-SNAPSHOT`.

#### Publish

```shell
./gradlew :jvm-package:jvm-publish-common:publishAllPublicationsToMavenRepository \
          publishLetsPlotJvmSwingPublicationToMavenRepository \
          publishLetsPlotJvmBatikPublicationToMavenRepository \
          publishLetsPlotGISPublicationToMavenRepository \
          publishLetsPlotCoreModulesToMavenRepository \
          publishLetsPlotIdeaPluginPublicationToMavenRepository
```

#### Check Uploaded Artifacts

Check uploaded artifacts here:

https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/org/jetbrains/lets-plot/
                
#### Using SNAPSHOT artifacts

Add snapshot repository to the `repositories` section of the `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
    // SNAPSHOTS
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/")
}
```
         
To tell Gradle to bypass the local cache and check remote repositories for newer versions or changed metadata:

```shell
./gradlew build --refresh-dependencies
```

#### Snapshot Version of JS Artifacts
ToDo.