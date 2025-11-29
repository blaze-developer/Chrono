# Chrono
[![](https://jitpack.io/v/com.blazedeveloper/chrono.svg)](https://jitpack.io/#com.blazedeveloper/chrono)

Chrono is a logging, telemetry, and replay framework designed for FTC that enables *log replay*.
Chrono can write data to log files, and live telemetry streams both using the
[RLOG format](https://github.com/Mechanical-Advantage/AdvantageKit/blob/main/RLOG-SPEC.md) 
compatible with [AdvantageScope](https://docs.advantagescope.org).
 
It is inspired by [AdvantageKit](https://docs.advantagekit.org), and written from the ground up in Kotlin.

**Online documentation can be found [here](https://chrono.blazedeveloper.com).**

## Installation
Chrono is currently deployed with [Jitpack](https://jitpack.io). To install it, add Jitpack to your
maven repositories block, and add Chrono to your dependencies with whichever version you'd like, or
the most recent release [here](https://github.com/blaze-developer/Chrono/releases):

```groovy
repositories {
    maven { url = "https://jitpack.io" }
}

dependencies {
    // Other dependencies
    implementation 'com.blazedeveloper:chrono:vX.Y.Z'
}
```

## Disclaimer
While the infrastructure for log replay is entirely in place, currently I do not know how to
find an entry point for actually *running* a log replay.

In Chrono's current state, it is just a logging library, but once a suitable entry point for running
robot code on a programmer's computer arises, log replay will be fully supported.

If you have any suggestions, message me, or write a github issue. Thank you.
