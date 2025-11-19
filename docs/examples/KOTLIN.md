# Kotlin Example
```kotlin
class MyLinearOpMode : LoggedLinearOpMode() {
    override fun runLoggedOpMode() {
        // Initialization logic
        
        waitForStart()

        while(isActive) logCycle {
            // Iterative logic
        }
    }
}
```