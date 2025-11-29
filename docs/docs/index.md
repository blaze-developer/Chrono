---
title: 'Introduction'
sidebar_position: 1
slug: '/'
---

# Chrono

Chrono is a logging, telemetry, and replay framework designed for FTC that enables *log replay*.
Chrono can write data to log files, and live telemetry streams both using the
[RLOG format](https://github.com/Mechanical-Advantage/AdvantageKit/blob/main/RLOG-SPEC.md)
compatible with [AdvantageScope](https://docs.advantagescope.org).

It is inspired by [AdvantageKit](https://docs.advantagekit.org), and written from the ground up in Kotlin.

:::tip
For a deeper understanding, and more information about **log replay** or **deterministic logging**,
refer to FRC 6328's documentation on AdvantageKit [here](https://docs.advantagekit.org).
:::

---
## Installation

[![](https://jitpack.io/v/com.blazedeveloper/chrono.svg)](https://jitpack.io/#com.blazedeveloper/chrono)

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