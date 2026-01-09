---
title: "Output Logging"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

Chrono's [output logging](https://docs.advantagekit.org/data-flow/recording-outputs/)
is achieved by giving the logger the key and value to log:

<Tabs groupId="lang" queryString="lang">
<TabItem value="kt" label="Kotlin" default>

```kotlin
Logger.output("YourKey", "YourValue")
Logger.output("Lift/Setpoint", setpointPosition)
Logger.output("RobotState", State.COLLECTING) // Enum
```
in
</TabItem>
<TabItem value="java" label="Java">

```java
Logger.output("YourKey", "YourValue");
Logger.output("Lift/Setpoint", setpointPosition);
Logger.output("RobotState", State.COLLECTING); // Enum
```

</TabItem>
</Tabs>

:::info
For information on the theory of what a logged output is, check out
[the AdvantageKit documentation](https://docs.advantagekit.org/data-flow/recording-outputs/).
:::