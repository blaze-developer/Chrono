# Java Example
Java's lambda syntax may make the logCycle helper look very verbose, so you may optionally call
``preCycle`` and ``postCycle`` manually:

```java
public class OpModeJava extends LoggedLinearOpMode {
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