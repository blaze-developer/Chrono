# Setup (Linear)
This is how you set up a Chrono linear opmode in Kotlin.

After installing, make a linear opmode that extends from ``LoggedLinearOpMode``, and implement the
``startLoggedOpMode()`` function.

## Loops
The ''Logger'' has ``preUser`` and ``postUser`` methods that must be called before and after user code for every robot iteration.
This is necessary for handling the log table, replay sources, timestamps, and log receivers. To ease
in this requirement, Chrono provides ``loggedCycle``, a method to wrap your robot iterations in that
automatically handles Logger iterations, and built-in data logging.

It should be used in your main control-flow loops, like a top level while loop.

```kotlin
while(isActive) loggedCycle {
    // Iterative logic
}
```

## Deterministic Inputs
Chrono handles and logs a few built-in inputs, including [synchronized timestamps](https://docs.advantagekit.org/data-flow/deterministic-timestamps),
gamepads, and lifecycle events like opmode init, active, and stop requests.

### Timestamps
Although Chrono cannot override built in methods like ``System.nanoTime()`` with its own time source,
it provides ``Logger.timestamp`` as the way to fetch a deterministic and synchronized
timestamp. This should be used for all replayed logic, and methods like ``System.nanoTime()`` should
only be used in hardware implementations / non-replayed logic.

### Gamepads
Gamepads are logged and replayed straight to the same API, so continue using them as normal.

### Lifecycle Events
Opmode lifecycle events need to come from a logged and deterministic source to guarantee replay
accuracy. In your opmodes, ensure that you always use members ``isActive``, ``inInit``, and``shouldStop``
as opposed to the methods ``opModeIsActive``, ``opModeInInit``, and ``isStopRequested``.

## Example
```kotlin
class MyLinearOpMode : LoggedLinearOpMode() {
    override fun runLoggedOpMode() {
        // Initialization logic
        
        waitForStart()

        while(isActive) loggedCycle {
            // Iterative logic
        }
    }
}
```