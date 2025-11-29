---
title: "Example Setups"
sidebar_position: 3
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs>
<TabItem value="kt" label="Kotlin" default>

```kotlin
class MyLinearOpMode : LoggedLinearOpMode() {
    init {
        Logger.receivers += RLOGWriter()
        Logger.receivers += RLOGServer()
        
        Logger.metadata += "FTCYear" to "2025-2026"
    }
    
    override fun runLoggedOpMode() {
        // Initialization logic
        
        waitForStart()

        while(isActive) logCycle {
            // Iterative logic
        }
    }
}
```

</TabItem>
<TabItem value="java" label="Java">

Java's lambda syntax may make the logCycle helper look very verbose, so you may call
``preCycle`` and ``postCycle`` manually:

```java
public class OpModeJava extends LoggedLinearOpMode {
    public OpModeJava() {
        Logger.addReceiver(new RLOGWriter());
        Logger.addReceiver(new RLOGServer());
        
        Logger.addMetadata("FTCYear", "2025-2026");
    }
    
    @Override
    public void runLoggedOpMode() {
        // Initialization logic
        
        waitForStart();
        
        while (isActive()) {
            preCycle();
            // Iterative logic
            postCycle();
        }
    }
}
```

</TabItem>
</Tabs>
