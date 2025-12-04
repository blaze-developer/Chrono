---
title: "Version Control"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

:::warning
When implementing this, keep in mind that:
- Your robot code should be checked out from a Git repository.
- The device that you build your robot code on must have git installed.
:::

For logging Git version control information at runtime, we recommend the
gradle plugin [GVersion](https://github.com/lessthanoptimal/gversion-plugin).
This allows your code to be aware of its Git versions.

## Installation

To install GVersion, add the following plugin to your gradle configuration:
```groovy title="TeamCode/build.gradle"
plugins {
    id "com.peterabeles.gversion" version "1.10.3"
}
```

Then, configure GVersion with your own timezone, date formats, language, etc.:

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```groovy title="TeamCode/build.gradle"
gversion {
    srcDir       = "src/main/java/"
    classPackage = "org.firstinspires.ftc.teamcode"
    dateFormat   = "MMM d yyyy HH:mm:ss z"
    timeZone     = "PST" // Enter your timezone here
    debug        = false
    language     = "kotlin"
    indent       = "    "
}
```

</TabItem>
<TabItem value="java" label="Java">

```groovy title="TeamCode/build.gradle"
gversion {
    srcDir       = "src/main/java/"
    classPackage = "org.firstinspires.ftc.teamcode"
    dateFormat   = "MMM d yyyy HH:mm:ss z"
    timeZone     = "PST" // Enter your timezone here
    debug        = false
    language     = "java"
    indent       = "    "
}
```

</TabItem>
</Tabs>

Finally, run GVersion every build by adding:
```groovy title="TeamCode/build.gradle"
project(":TeamCode").preBuild.dependsOn(createVersionFile)
```

Run a gradle sync and a build, and then a file named ``GVersion`` will become available.

:::warning
This file updates with the latest information every time you build your robot code,
and **should not** be checked into git. Add it to your ``.gitignore``:

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```gitignore title=".gitignore"
# GVersion
GVersion.kt
```

</TabItem>
<TabItem value="java" label="Java">

```gitignore title=".gitignore"
# GVersion
GVersion.java
```

</TabItem>
</Tabs>
:::

## Metadata Logging

Now that you can access version control constants at runtime, use it however you would like.
Here's an example for logging that information as metadata:

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```kotlin
Logger.metadata += "Project" to MAVEN_GROUP
Logger.metadata += "BuildDate" to BUILD_DATE
Logger.metadata += "GitHash" to GIT_SHA
Logger.metadata += "GitBranch" to GIT_BRANCH
Logger.metadata += "GitDate" to GIT_DATE
Logger.metadata += "GitStatus" to when(DIRTY) {
    0 -> "All Changes Commited"
    1 -> "Uncommited Changes"
    else -> "Unknown"
}
```

</TabItem>
<TabItem value="java" label="Java">

```java
Logger.addMetadata("Project", GVersion.MAVEN_GROUP);
Logger.addMetadata("BuildDate", GVersion.BUILD_DATE);
Logger.addMetadata("GitHash", GVersion.GIT_SHA);
Logger.addMetadata("GitBranch", GVersion.GIT_BRANCH);
Logger.addMetadata("GitDate", GVersion.GIT_DATE);
switch (GVersion.DIRTY) {
    case 0:
        Logger.recordMetadata("GitStatus", "All changes committed");
        break;
    case 1:
        Logger.recordMetadata("GitStatus", "Uncomitted changes");
        break;
    default:
        Logger.recordMetadata("GitStatus", "Unknown");
        break;
}
```

</TabItem>
</Tabs>
