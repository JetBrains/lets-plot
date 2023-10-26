## Publishing SNAPSHOT versions of JVM artifacts.

### 1. Prepare for publishing:

Just add token to the `sonatype` section of the `build_settings.yml` file.

### 2. Publish:

Run tne next gradle tasks from the project root:

```shell
./gradlew publishLetsPlotJvmJfxPublicationToMavenRepository \
          publishLetsPlotJvmBatikPublicationToMavenRepository \
          publishLetsPlotImageExportPublicationToMavenRepository \
          publishLetsPlotCoreModulesToMavenRepository
```

### 3. Check uploaded artifacts:

Check uploaded artifacts here:

https://oss.sonatype.org/content/repositories/snapshots/org/jetbrains/lets-plot/
