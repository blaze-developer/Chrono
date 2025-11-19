# Logger Configuration
You should configure the Logger BEFORE it it started in either an *initializer* (in Kotlin), or your
*constructor* (in Java). The Logger is automatically started by ``LoggedLinearOpMode`` after
constructors and initializers.

**Examples of Logger configuration can be found [here](./examples/KOTLIN.md) for Kotlin and
[here](./examples/JAVA.md) for Java.**

## Log Receivers
A log receiver is something that accepts the current cycle's logged data, and puts it somewhere
like a log file or stream. Receivers currently included in Chrono are ``RLOGServer`` and
``RLOGWriter``.

In Kotlin, receivers can be added using the ``+=`` operator, or a method:
```kotlin
init {
    // Adding an RLOG stream using the += operator:
    Logger.receivers += RLOGServer()

    // Adding an RLOG file writer using the method:
    Logger.addReceiver(RLOGWriter())
}
```

In Java, receivers can be added like this in your constructor:
```java
public YourOpMode() {
    Logger.addReceiver(new RLOGServer());
}
```

## Metadata
Metadata is data *about* your log, [and can be viewed in AdvantageScope](https://docs.advantagescope.org/tab-reference/metadata).

Some examples of metadata include:

 - Git versions and dates
 - Runtime environments
 - Your robot's name

Note that it is entirely up to *you* what metadata you do and don't include in your logs.

Metadata must be added to the Logger **BEFORE** it is started, in an initializer or constructor.
Similarly to receivers, it can be added with a += operator in Kotlin, or a method in both Kotlin
and Java.

In Kotlin:
```kotlin
init {
    // Adding metadata with the += operator:
    Logger.metadata += "ProjectName" to "2025RobotCode"
    
    // Adding metadata with the method:
    Logger.addMetadata("GitHash", GVersion.GIT_HASH)
}
```

Or in Java:
```java
public YourOpMode() {
    Logger.addMetadata("FTCYear", "2025");
}
```