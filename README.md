# Chrono
[![](https://jitpack.io/v/com.blazedeveloper/chrono.svg)](https://jitpack.io/#com.blazedeveloper/chrono)

Chrono is a logging, telemetry, and replay framework designed for android-based First Tech Challenge robots that has the framework to enable *log replay*.
Chrono can write data to log files, and live telemetry streams both using the
[RLOG format](https://github.com/Mechanical-Advantage/AdvantageKit/blob/main/RLOG-SPEC.md) 
compatible with [AdvantageScope](https://docs.advantagescope.org).
 
It is inspired by [AdvantageKit](https://docs.advantagekit.org), and written from the ground up in Kotlin.

This project originated when I was first learning the ins and outs of the existing Android SDK control system in a high school introductory robotics class created in cooperation with the
high school. A few simple FTC games were designed as the curriculum, and small teams of 4-5 students created FTC robots to play that game. As a senior on my FRC team, I already had plenty of experience, and tried my best to help brand new students with no programming experience.

When it came to programming my own robot, I had extra time due to the simplicity of the robots, was in love with the AdvantageKit structure with IO interfaces, and set out to recreate it as an exercise in the Kotlin programming language, Kotlin+Java interoperability, Docusaurus, CI, and a deeper understanding of project development and design.

### **Online documentation can be found [here](https://chrono.blazedeveloper.com).**

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

In Chrono's current state, it is just a logging library, providing a similar experience to AdvantageKit. But if a suitable entry point for running
robot code on a programmer's computer, and selecting a log file, arises, log replay will be fully supported.

If you have any suggestions, message me, or write a issue. Thank you.
