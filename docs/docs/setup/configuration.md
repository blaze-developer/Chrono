---
title: "Logger Configuration"
sidebar_position: 1
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

You should configure the Logger BEFORE it it started in either an *initializer* (in Kotlin), or your
*constructor* (in Java). The Logger is automatically started by ``LoggedLinearOpMode`` after
constructors and initializers.

**Examples of Logger configuration can be found [here](./example).**

## Log Receivers
A log receiver is something that accepts the current cycle's logged data, and puts it somewhere
like a log file or stream. Receivers currently included in Chrono are ``RLOGServer`` and
``RLOGWriter``.

<Tabs>
<TabItem value="kt" label="Kotlin" default>

In Kotlin, receivers can be added using the ``+=`` operator, or a method:
```kotlin
init {
    // Adding an RLOG stream using the += operator:
    Logger.receivers += RLOGServer()

    // Adding an RLOG file writer using the method:
    Logger.addReceiver(RLOGWriter())
}
```

</TabItem>
<TabItem value="java" label="Java">

In Java, receivers can be added like this in your constructor:
```java
public YourOpMode() {
    Logger.addReceiver(new RLOGServer());
}
```

</TabItem>
</Tabs>

## Metadata
Metadata is data *about* your log, [and can be viewed in AdvantageScope](https://docs.advantagescope.org/tab-reference/metadata).

Some examples of metadata include:

 - Git versions and dates
 - Runtime environments
 - Your robot's name

Note that it is entirely up to *you* what metadata you do and don't include in your logs.

<Tabs>
<TabItem value="kt" label="Kotlin" default>

```kotlin
init {
    // Adding metadata with the += operator:
    Logger.metadata += "ProjectName" to "2025RobotCode"

    // Adding metadata with the method:
    Logger.addMetadata("GitHash", GVersion.GIT_HASH)
}
```

</TabItem>
<TabItem value="java" label="Java">

```java
public YourOpMode() {
    Logger.addMetadata("FTCYear", "2025");
}
```

</TabItem>
</Tabs>