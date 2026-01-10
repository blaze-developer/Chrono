---
title: "Logged Data"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

:::info
Log data is stored in the same way as AdvantageKit, with string keys and
slashes used to denote subtables. **All logged data is persistent and will stay the same
until updated.**
:::

The following are the data types that Chrono supports input and output logging for:

### Simples
Chrono supports these simple types and their arrays:

 - Raw Bytes (Byte, ByteArray)
 - Booleans (Boolean, BooleanArray)
 - Integers / Longs
 - Floats / Doubles
 - Strings

### Enums
Chrono also supports logging and replaying generic enum values and enum arrays.
They are represented by string values from the enum's ``name()`` method.

:::note
Enum logging requires Java users to pass the class of your enum to retrieve it from the log table.
This is unnecessary for Kotlin users. Check the example below.

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```kotlin
override fun fromLog(table: LogTable) {
    state = table.get("State", state)
    stateArray = table.get("StateArray", stateArray)
}
```
</TabItem>
<TabItem value="java" label="Java" default>

```java
@Override
public void fromLog(LogTable table) {
    state = table.get("State", state, State.class);
    stateArray = table.get("StateArray", stateArray, State.class);
}
```
</TabItem>
</Tabs>
:::

### Normalized Colors
Chrono also supports logging and replaying ``NormalizedRGBA`` objects from color sensors.
These are represented by float arrays, storing the colors components in order:
 - Red Channel
 - Green Channel
 - Blue Channel
 - Alpha / Opacity
