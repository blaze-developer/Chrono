---
title: "Input Logging"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

Input logging in Chrono is nearly identical to AdvantageKit.
All robot input data, (sensors, encoders, vision, etc.) must be logged in order to accurately
recreate a log file's conditions in log replay.

A common structure to ensure this is *hardware abstraction*, that is, separating hardware and
subsystem logic to maximize flexibility and pipe input data through a common place. This separation
is called an *IO interface*.

:::tip
For those not familiar with AdvantageKit, it is highly recommended that you check out the
[AdvantageKit documentation](https://docs.advantagekit.org/data-flow/recording-inputs/io-interfaces) for more information on this structure. This documentation
will mainly emphasize how Chrono implements the theory that pre-exists and is documented
in AdvantageKit.
:::

## Input Schemas
Input data should flow through ``LoggableInput`` objects, which define the structure of input
to the subsystem implement methods to save and load data from a log file. Subsystems then reference
these object rather than directly querying hardware.

Examples of defining these input objects are as follows:

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```kotlin
class DriveInputs : LoggableInputs {
    var yawRads = 0.0

    var flPos = 0.0
    var frPos = 0.0
    var blPos = 0.0
    var brPos = 0.0

    override fun toLog(table: LogTable) {
        table.put("YawRads", yawRads)
        table.put("PitchRads", pitchRads)
        table.put("RollRads", rollRads)
        table.put("FlPos", flPos)
        table.put("FrPos", frPos)
        table.put("BlPos", blPos)
        table.put("BrPos", brPos)
    }

    override fun fromLog(table: LogTable) {
        yawRads = table.get("YawRads", yawRads)
        pitchRads = table.get("PitchRads", pitchRads)
        rollRads = table.get("RollRads", rollRads)
        flPos = table.get("FlPos", flPos)
        frPos = table.get("FrPos", frPos)
        blPos = table.get("BlPos", blPos)
        brPos = table.get("BrPos", brPos)
    }
}
```
</TabItem>
</Tabs>

:::tip
For Kotlin users, [automatic input logging](#automatic-input-logging) is available to skip writing these repetitive method implementations.
:::

## Input Processing
In your subsystem periodic, this object can then be updated with new data, and these objects are passed
to the ``Logger`` for processing.

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```kotlin
// Beginning of periodic loop, before your logic.
io.updateInputs(inputs)
Logger.processInputs(inputs)

// Your periodic logic continues here.
```
</TabItem>
<TabItem value="java" label="Java" default>

```java
// Beginning of periodic loop, before your logic.
io.updateInputs(inputs);
Logger.processInputs(inputs);

// Your periodic logic continues here.
```
</TabItem>
</Tabs>

:::warning
This should be done every subsystem periodic loop **before** referencing inputs. This ensures the
current cycle is logged and synchronous.
:::

## Automatic Input Logging
For kotlin users looking to simplify their code and skip writing repetitive logtable operations,
your input schemas can extend from the abstract class ``AutoLoggableInputs``, using delegates
for input fields.

The below is an example of the same input object from above, but changed to make use
of automatic input logging.

```kt
class DriveInputs : AutoLoggableInputs() {
    var yawRads by logged("YawRads", 0.0)
    var pitchRads by logged("PitchRads", 0.0)
    var rollRads by logged("RollRads", 0.0)

    var flPos by logged("FlPos", 0.0)
    var frPos by logged("FrPos", 0.0)
    var blPos by logged("BlPos", 0.0)
    var brPos by logged("BrPos", 0.0)
}
```

:::warning
This feature is not available for Java users, as the implementation
relies heavily on Kotlin's language features.
:::