# Setup (Linear)
This is how you set up a Chrono linear opmode in Kotlin.

After installing, make a linear opmode that extends from ``LoggedLinearOpMode``, and implement the
``startLoggedOpMode()`` function.

**Examples of setup can be found [here](examplesOTLIN.md) for Kotlin and
[here](examplesAVA.md) for Java.**

## Loops
The ''Logger'' has ``preUser`` and ``postUser`` methods that must be called before and after user code for every robot iteration.
This is necessary for handling the log table, replay sources, timestamps, and log receivers.

Chrono provides ``logCycle``, a method to wrap your robot iterations in that
automatically handles Logger iterations, and built-in data logging. For Java, the user may manually
call ``preCycle`` and ``postCycle``, as the ``logCycle`` helper may look a little verbose.

It should be used in your main control-flow loops, like a top level while loop.

## Deterministic Inputs
Chrono handles and logs a few built-in inputs, including [synchronized timestamps](https://docs.advantagekit.org/data-flow/deterministic-timestamps),
gamepads, and lifecycle events like opmode init, active, and stop requests.

### Timestamps
Although Chrono cannot override built in methods like ``System.nanoTime`` with its own time source,
it provides ``Logger.timestamp`` as the way to fetch a deterministic and synchronized
timestamp. This should be used for all replayed logic, and methods like ``System.nanoTime`` should
only be used in hardware implementations / non-replayed logic.

### Gamepads
Gamepads are logged and replayed straight to the same API, so continue using them as normal.

### Lifecycle Events
Opmode lifecycle events need to come from a logged and deterministic source to guarantee replay
accuracy. In your opmodes, ensure that you always use members ``isActive``, ``inInit``, and``shouldStop``
as opposed to the methods ``opModeIsActive``, ``opModeInInit``, and ``isStopRequested``.

## Automatic Outputs
Along with the automatic inputs, Chrono automatically logs a few outputs as well. Including Logger
and Opmode loop timings under "LoggerTimings", and captured console data from ``System.out`` and
``System.err`` [viewable in AdvantageScope](https://docs.advantagescope.org/tab-reference/console).