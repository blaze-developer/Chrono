# Kotlin Example
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